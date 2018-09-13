package com.example.dao;

import com.example.pojo.RedPacket;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface RedPacketDao {
    RedPacket getRedPacket(Long id);

    RedPacket getRedPacketForUpdate(Long id);

    int decreaseRedPacket(Long id);

    int decreaseRedPacketForVersion(@Param("id") Long id, @Param("version") Integer version);
}
