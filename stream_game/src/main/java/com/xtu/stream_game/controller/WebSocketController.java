package com.xtu.stream_game.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.messaging.handler.annotation.Payload;

import java.util.HashMap;
import java.util.Map;

@Controller
public class WebSocketController {

    @MessageMapping("/chat")
    @SendTo("/topic/messages")
    public Map<String, Object> handleMessage(@Payload Map<String, String> message) {
        Map<String, Object> response = new HashMap<>();
        response.put("type", "chat");
        response.put("content", message.get("content"));
        response.put("sender", message.get("sender"));
        response.put("timestamp", System.currentTimeMillis());
        return response;
    }

    @MessageMapping("/notification")
    @SendTo("/topic/notifications")
    public Map<String, Object> handleNotification(@Payload Map<String, String> notification) {
        Map<String, Object> response = new HashMap<>();
        response.put("type", "notification");
        response.put("message", notification.get("message"));
        response.put("timestamp", System.currentTimeMillis());
        return response;
    }
} 