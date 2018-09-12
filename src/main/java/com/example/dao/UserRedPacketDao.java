package com.example.dao;

import com.example.pojo.UserRedPacket;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRedPacketDao {
    int grabRedPacket(UserRedPacket packet);
}
