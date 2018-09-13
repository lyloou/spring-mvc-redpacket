package com.example.controller;

import com.example.service.UserRedPacketService;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("userRedPacket")
public class UserRedPacketController {

    @Autowired
    private UserRedPacketService userRedPacketService = null;

    @RequestMapping(value = "/grabRedPacket")
    @ResponseBody
    public Map<String, Object> grabRedPacket(@Param("redPacketId") Long redPacketId, @Param("userId") Long userId) {
        int result = userRedPacketService.grabRedPacket(redPacketId, userId);
        Map<String, Object> retMap = new HashMap<>();
        boolean flag = result > 0;
        retMap.put("success", flag);
        retMap.put("message", flag ? "抢红包成功" : "抢红包失败");
        return retMap;
    }

    @RequestMapping(value = "/grabRedPacketForUpdate")
    @ResponseBody
    public Map<String, Object> grabRedPacketForUpdate(@Param("redPacketId") Long redPacketId, @Param("userId") Long userId) {
        int result = userRedPacketService.grabRedPacketForUpdate(redPacketId, userId);
        Map<String, Object> retMap = new HashMap<>();
        boolean flag = result > 0;
        retMap.put("success", flag);
        retMap.put("message", flag ? "抢红包成功" : "抢红包失败");
        return retMap;
    }

    @RequestMapping(value = "/grabRedPacketForVersion")
    @ResponseBody
    public Map<String, Object> grabRedPacketForVersion(@Param("redPacketId") Long redPacketId, @Param("userId") Long userId) {
        int result = userRedPacketService.grabRedPacketForVersion(redPacketId, userId);
        Map<String, Object> retMap = new HashMap<>();
        boolean flag = result > 0;
        retMap.put("success", flag);
        retMap.put("message", flag ? "抢红包成功" : "抢红包失败");
        return retMap;
    }

    @RequestMapping(value = "/grabRedPacketByRedis")
    @ResponseBody
    public Map<String, Object> grabRedPacketByRedis(@Param("redPacketId") Long redPacketId, @Param("userId") Long userId) {
        Long result = userRedPacketService.grabRedPacketByRedis(redPacketId, userId);
        Map<String, Object> retMap = new HashMap<>();
        boolean flag = result != null && result > 0;
        retMap.put("success", flag);
        retMap.put("message", flag ? "抢红包成功" : "抢红包失败");
        return retMap;
    }

    @RequestMapping(value = "/grab")
    public String grab() {
        return "grab";
    }
}
