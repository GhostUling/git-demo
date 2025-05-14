package com.xtu.stream_game.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
public class HomeController {
    
    private static final Logger logger = LoggerFactory.getLogger(HomeController.class);
    
    @GetMapping("/")
    public String home() {
        logger.info("访问根路径，重定向到pages/index.html");
        return "redirect:/pages/index.html";
    }
} 