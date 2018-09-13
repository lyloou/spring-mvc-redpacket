package com.example.dao;

import com.example.pojo.RedPacket;
import org.springframework.stereotype.Repository;

@Repository
public interface RedPacketDao {
    RedPacket getRedPacket(Long id);

    RedPacket getRedPacketForUpdate(Long id);

    int decreaseRedPacket(Long id);
}
