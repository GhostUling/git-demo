package com.xtu.stream_game.controller;

import org.junit.jupiter.api.Test;
import java.util.HashMap;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

class WebSocketControllerTest {

    private final WebSocketController controller = new WebSocketController();

    @Test
    @DisplayName("处理聊天消息 - 正确构造响应")
    void handleMessage_CorrectResponseStructure() {
        // 准备输入数据
        Map<String, String> message = new HashMap<>();
        message.put("content", "Hello World");
        message.put("sender", "player1");

        // 调用方法
        Map<String, Object> response = controller.handleMessage(message);

        // 验证响应结构
        assertAll(
            () -> assertEquals("chat", response.get("type")),
            () -> assertEquals("Hello World", response.get("content")),
            () -> assertEquals("player1", response.get("sender")),
            () -> assertNotNull(response.get("timestamp"))
        );
    }

    @Test
    @DisplayName("处理通知消息 - 正确构造响应")
    void handleNotification_CorrectResponseStructure() {
        // 准备输入数据
        Map<String, String> notification = new HashMap<>();
        notification.put("message", "系统维护通知");

        // 调用方法
        Map<String, Object> response = controller.handleNotification(notification);

        // 验证响应结构
        assertAll(
            () -> assertEquals("notification", response.get("type")),
            () -> assertEquals("系统维护通知", response.get("message")),
            () -> assertNotNull(response.get("timestamp"))
        );
    }
}