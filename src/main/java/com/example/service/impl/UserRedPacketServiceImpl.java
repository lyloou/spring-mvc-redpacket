package com.example.service.impl;

import com.example.dao.RedPacketDao;
import com.example.dao.UserRedPacketDao;
import com.example.pojo.RedPacket;
import com.example.pojo.UserRedPacket;
import com.example.service.RedisRedPacketService;
import com.example.service.UserRedPacketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisDataException;

@Service
public class UserRedPacketServiceImpl implements UserRedPacketService {
    private static final int FAILED = 0;

    @Autowired
    private UserRedPacketDao userRedPacketDao = null;

    @Autowired
    private RedPacketDao redPacketDao = null;

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public int grabRedPacket(Long redPacketId, Long userId) {
        RedPacket redPacket = redPacketDao.getRedPacket(redPacketId);
        if (redPacket != null && redPacket.getStock() > 0) {
            redPacketDao.decreaseRedPacket(redPacketId);

            UserRedPacket userRedPacket = new UserRedPacket();
            userRedPacket.setRedPacketId(redPacketId);
            userRedPacket.setUserId(userId);
            userRedPacket.setAmount(redPacket.getAmount());
            userRedPacket.setNote("抢红包 " + redPacketId);

            int result = userRedPacketDao.grabRedPacket(userRedPacket);
            return result;
        }
        return FAILED;
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public int grabRedPacketForUpdate(Long redPacketId, Long userId) {
        RedPacket redPacket = redPacketDao.getRedPacketForUpdate(redPacketId);

        if (redPacket != null && redPacket.getStock() > 0) {
            redPacketDao.decreaseRedPacket(redPacketId);
            UserRedPacket userRedPacket = new UserRedPacket();
            userRedPacket.setRedPacketId(redPacketId);
            userRedPacket.setUserId(userId);
            userRedPacket.setAmount(redPacket.getAmount());
            userRedPacket.setNote("抢红包 " + redPacketId);

            int result = userRedPacketDao.grabRedPacket(userRedPacket);
            return result;
        }
        return FAILED;
    }

    @Override
    public int grabRedPacketForVersion(Long redPacketId, Long userId) {
        // try three times
        for (int i = 0; i < 3; i++) {
            RedPacket redPacket = redPacketDao.getRedPacket(redPacketId);

            if (redPacket != null && redPacket.getStock() > 0) {
                int update = redPacketDao.decreaseRedPacketForVersion(redPacketId, redPacket.getVersion());
                if (update == 0) {
                    continue;
                }

                UserRedPacket userRedPacket = new UserRedPacket();
                userRedPacket.setRedPacketId(redPacketId);
                userRedPacket.setUserId(userId);
                userRedPacket.setAmount(redPacket.getAmount());
                userRedPacket.setNote("抢红包 " + redPacketId);

                int result = userRedPacketDao.grabRedPacket(userRedPacket);
                return result;
            } else {
                return FAILED;
            }
        }

        return FAILED;
    }


    @Autowired
    RedisTemplate redisTemplate;

    @Autowired
    RedisRedPacketService redisRedPacketService;
    String script = "local listKey = 'red_packet_list_'..KEYS[1]\n" +
            "local redPacket = 'red_packet_'..KEYS[1]\n" +
            "local stock = tonumber(redis.call('hget', redPacket, 'stock'))\n" +
            "if stock <= 0 then return 0 end\n" +
            "stock = stock - 1\n" +
            "redis.call('hset', redPacket, 'stock', tostring(stock))\n" +
            "redis.call('rpush', listKey, ARGV[1])\n" +
            "if stock == 0 then return 2 end\n" +
            "return 1";
    String sha1;

    @Override
    public Long grabRedPacketByRedis(Long redPacketId, Long userId) {
        String args = userId + "-" + System.currentTimeMillis();
        Jedis jedis = (Jedis) redisTemplate.getConnectionFactory().getConnection().getNativeConnection();
        Long result = null;
        try {
            if (sha1 == null) {
                sha1 = jedis.scriptLoad(script);
            }
            Object res = jedis.evalsha(sha1, 1, redPacketId + "", args);
            result = (Long) res;
            if (result == 2) {
                String unitAmountStr = jedis.hget("red_packet_" + redPacketId, "unit_amount");
                double unitAmount = Double.parseDouble(unitAmountStr);
                System.out.println("thread_name = " + Thread.currentThread().getName());
                redisRedPacketService.saveUserRedPacketByRedis(redPacketId, unitAmount);
            }
        } catch (JedisDataException e) {
            System.out.println("--------------------------------------------");
            e.printStackTrace();
            System.out.println("--------------------------------------------");
        } finally {
            if (jedis != null && !jedis.isConnected()) {
                jedis.close();
            }
        }
        return result;
    }
}
