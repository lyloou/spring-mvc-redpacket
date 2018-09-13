## 测试网址

v1: http://localhost:8080/userRedPacket/grabRedPacket?redPacketId=1&userId=2691

v2: http://localhost:8080/userRedPacket/grabRedPacketForUpdate?redPacketId=1&userId=2691

v3: http://localhost:8080/userRedPacket/grabRedPacketForVersion?redPacketId=1&userId=2691

v4: http://localhost:8080/userRedPacket/grabRedPacketByRedis?redPacketId=4&userId=2691

注：
v1-v3需要配置好mysql：`resource/mysql.sql`  
v4需要先设置好redis  
```shell
hset red_packet_4 stock 10
hset red_packet_4 unit_amount 10
hget red_packet_4 stock
```

## 参考资料
- [Java EE 互联网轻量级框架整合开发，第22章](#)