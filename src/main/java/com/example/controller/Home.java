package com.example.controller;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class Home {

    private static final Logger logger = Logger.getLogger(Home.class);


    @RequestMapping("/")
    public String home() {
        logger.debug("=========== logger info =========== ");
        return "index";
    }
}
