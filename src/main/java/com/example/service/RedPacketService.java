package com.example.service;

import com.example.pojo.RedPacket;

public interface RedPacketService {
    RedPacket getRedPacket(Long id);

    int decreaseRedPacket(Long id);
}
