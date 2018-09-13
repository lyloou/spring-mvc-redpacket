package com.example.service.impl;

import com.example.pojo.UserRedPacket;
import com.example.service.RedisRedPacketService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

@Service
public class RedisRedPacketServiceImpl implements RedisRedPacketService {
    private static final Logger logger = Logger.getLogger(RedisRedPacketServiceImpl.class);
    private static final String PREFIX = "red_packet_list_";
    private static final int TIME_SIZE = 5;

    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private DataSource dataSource;

    @Async
    @Override
    public void saveUserRedPacketByRedis(Long redPacketId, Double unitAmount) {
        System.out.println("开始保存数据");
        long start = System.currentTimeMillis();
        BoundListOperations ops = redisTemplate.boundListOps(PREFIX + redPacketId);
        Long size = ops.size();

        long times = size % TIME_SIZE == 0 ? size / TIME_SIZE : size / TIME_SIZE + 1;
        int count = 0;
        List<UserRedPacket> userRedPacketList = new ArrayList<>(TIME_SIZE);
        for (int i = 0; i < times; i++) {
            List userIdList;
            if (i == 0) {
                userIdList = ops.range(0, TIME_SIZE);
            } else {
                userIdList = ops.range(i * TIME_SIZE + 1, (i + 1) * TIME_SIZE);
            }
            userRedPacketList.clear();
            for (int j = 0; j < userIdList.size(); j++) {
                String args = userIdList.get(j).toString();
                String[] arr = args.split("-");
                String userIdStr = arr[0];
                String timeStr = arr[1];
                long userId = Long.parseLong(userIdStr);
                long time = Long.parseLong(timeStr);
                UserRedPacket userRedPacket = new UserRedPacket();
                userRedPacket.setRedPacketId(redPacketId);
                userRedPacket.setUserId(userId);
                userRedPacket.setAmount(unitAmount);
                userRedPacket.setGrabTime(new Timestamp(time));
                userRedPacket.setNote("红包：" + redPacketId);
                userRedPacketList.add(userRedPacket);
            }
            count += executeBatch(userRedPacketList);
        }
        redisTemplate.delete(PREFIX + redPacketId);
        long end = System.currentTimeMillis();
        System.out.println("保存数据结束，耗时：" + (end - start) + "ms");
        System.out.println("共：" + count + "条记录");

    }

    private int executeBatch(List<UserRedPacket> userRedPacketList) {
        Connection conn = null;
        int[] count;
        try {
            conn = dataSource.getConnection();
            conn.setAutoCommit(false);
            Statement stmt = conn.createStatement();
            for (UserRedPacket userRedPacket : userRedPacketList) {
                String sql1 = "update t_red_packet set stock = stock-1 where id=" + userRedPacket.getRedPacketId();
                DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String sql2 = "insert into t_user_red_packet(red_packet_id, user_id, amount, grab_time, note)"
                        + " values ("
                        + userRedPacket.getRedPacketId() + ", "
                        + userRedPacket.getUserId() + ", "
                        + userRedPacket.getAmount() + ", "
                        + "'" + df.format(userRedPacket.getGrabTime()) + "', "
                        + "'" + userRedPacket.getNote() + "')";
                stmt.addBatch(sql1);
                stmt.addBatch(sql2);
                logger.info(sql1);
                logger.info(sql2);
            }
            count = stmt.executeBatch();
            conn.commit();
        } catch (SQLException e) {
            throw new RuntimeException("批量插入数据库错误");
        } finally {
            try {
                if (conn != null && !conn.isClosed()) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return count.length / 2;
    }

}
