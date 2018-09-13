package com.example.service;

public interface UserRedPacketService {
    int grabRedPacket(Long redPacketId, Long userId);

    int grabRedPacketForUpdate(Long redPacketId, Long userId);

    int grabRedPacketForVersion(Long redPacketId, Long userId);
}
