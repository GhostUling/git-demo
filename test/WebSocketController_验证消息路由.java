package com.xtu.stream_game.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class WebSocketControllerIntegrationTest {

    private WebSocketStompClient stompClient;
    private BlockingQueue<Map<String, Object>> chatResponses;
    private BlockingQueue<Map<String, Object>> notificationResponses;

    @BeforeEach
    void setup() {
        this.stompClient = new WebSocketStompClient(new SockJsClient(
            Collections.singletonList(new WebSocketTransport(new StandardWebSocketClient()))
        ));
        stompClient.setMessageConverter(new MappingJackson2MessageConverter());
        chatResponses = new LinkedBlockingDeque<>();
        notificationResponses = new LinkedBlockingDeque<>();
    }

    @Test
    @DisplayName("聊天消息应路由到/topic/messages")
    void chatMessageRouting() throws Exception {
        // 建立连接并订阅主题
        StompSession session = stompClient.connect("ws://localhost:{port}/stream-game-websocket", 
            new StompSessionHandlerAdapter() {}, 8080).get(5, TimeUnit.SECONDS);
        session.subscribe("/topic/messages", new StompFrameHandlerImpl(chatResponses));

        // 发送消息到/chat端点
        Map<String, String> message = Map.of(
            "content", "集成测试消息",
            "sender", "tester"
        );
        session.send("/app/chat", message);

        // 验证响应
        Map<String, Object> response = chatResponses.poll(5, TimeUnit.SECONDS);
        assertAll(
            () -> assertEquals("chat", response.get("type")),
            () -> assertEquals("集成测试消息", response.get("content")),
            () -> assertEquals("tester", response.get("sender"))
        );
    }

    @Test
    @DisplayName("通知消息应路由到/topic/notifications")
    void notificationMessageRouting() throws Exception {
        // 建立连接并订阅主题
        StompSession session = stompClient.connect("ws://localhost:{port}/stream-game-websocket", 
            new StompSessionHandlerAdapter() {}, 8080).get(5, TimeUnit.SECONDS);
        session.subscribe("/topic/notifications", new StompFrameHandlerImpl(notificationResponses));

        // 发送消息到/notification端点
        Map<String, String> notification = Map.of("message", "测试通知");
        session.send("/app/notification", notification);

        // 验证响应
        Map<String, Object> response = notificationResponses.poll(5, TimeUnit.SECONDS);
        assertAll(
            () -> assertEquals("notification", response.get("type")),
            () -> assertEquals("测试通知", response.get("message"))
        );
    }

    // STOMP消息处理器
    private static class StompFrameHandlerImpl implements StompFrameHandler {
        private final BlockingQueue<Map<String, Object>> responseQueue;

        public StompFrameHandlerImpl(BlockingQueue<Map<String, Object>> responseQueue) {
            this.responseQueue = responseQueue;
        }

        @Override
        public Type getPayloadType(StompHeaders headers) {
            return Map.class;
        }

        @Override
        public void handleFrame(StompHeaders headers, Object payload) {
            responseQueue.offer((Map<String, Object>) payload);
        }
    }
}