package com.example.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("userRedPacket")
public class UserRedPacketController {

    @RequestMapping(value = "/grabRedPacket")
    @ResponseBody
    public Map<String, Object> grabRedPacket() {
        Map<String, Object> retMap = new HashMap<>();
        boolean flag = false;
        retMap.put("success", flag);
        retMap.put("message", flag ? "抢红包成功" : "抢红包失败");
        return retMap;
    }
}
