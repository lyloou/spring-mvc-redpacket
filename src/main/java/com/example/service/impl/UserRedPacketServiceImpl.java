package com.example.service.impl;

import com.example.dao.RedPacketDao;
import com.example.dao.UserRedPacketDao;
import com.example.pojo.RedPacket;
import com.example.pojo.UserRedPacket;
import com.example.service.UserRedPacketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

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
        RedPacket redPacket = redPacketDao.getRedPacket(redPacketId);

        if (redPacket != null && redPacket.getStock() > 0) {
            int update = redPacketDao.decreaseRedPacketForVersion(redPacketId, redPacket.getVersion());
            if (update == 0) {
                return FAILED;
            }

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
}
