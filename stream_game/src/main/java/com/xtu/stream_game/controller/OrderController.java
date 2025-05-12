package com.xtu.stream_game.controller;

import com.xtu.stream_game.entity.Order;
import com.xtu.stream_game.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping
    public ResponseEntity<?> createOrder(
            @RequestParam Integer playerId,
            @RequestParam Integer gameId,
            @RequestParam(required = false) String description) {
        try {
            Order order = orderService.createOrder(playerId, gameId, description);
            return ResponseEntity.ok(order);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", "创建订单失败",
                    "message", e.getMessage()
            ));
        }
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<?> getOrder(@PathVariable Integer orderId) {
        try {
            Order order = orderService.getOrderById(orderId);
            return ResponseEntity.ok(order);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", "获取订单失败",
                    "message", e.getMessage()
            ));
        }
    }

    @GetMapping("/player/{playerId}")
    public ResponseEntity<?> getPlayerOrders(@PathVariable Integer playerId) {
        try {
            List<Order> orders = orderService.getPlayerOrders(playerId);
            return ResponseEntity.ok(orders);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", "获取玩家订单失败",
                    "message", e.getMessage()
            ));
        }
    }

    @GetMapping("/player/{playerId}/status/{status}")
    public ResponseEntity<?> getPlayerOrdersByStatus(
            @PathVariable Integer playerId,
            @PathVariable Order.OrderStatus status) {
        try {
            List<Order> orders = orderService.getPlayerOrdersByStatus(playerId, status);
            return ResponseEntity.ok(orders);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", "获取玩家订单失败",
                    "message", e.getMessage()
            ));
        }
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<?> getAllOrdersByStatus(@PathVariable Order.OrderStatus status) {
        try {
            List<Order> orders = orderService.getAllOrdersByStatus(status);
            return ResponseEntity.ok(orders);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", "获取订单失败",
                    "message", e.getMessage()
            ));
        }
    }

    @PostMapping("/{orderId}/status")
    public ResponseEntity<?> updateOrderStatus(
            @PathVariable Integer orderId,
            @RequestBody Map<String, Object> request) {
        try {
            Order.OrderStatus status = Order.OrderStatus.valueOf(request.get("status").toString());
            String transactionId = (String) request.get("transactionId");
            Order order = orderService.updateOrderStatus(orderId, status, transactionId);
            return ResponseEntity.ok(order);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", "更新订单状态失败",
                    "message", e.getMessage()
            ));
        }
    }

    @PostMapping("/{orderId}/cancel")
    public ResponseEntity<?> cancelOrder(@PathVariable Integer orderId) {
        try {
            Order order = orderService.cancelOrder(orderId);
            return ResponseEntity.ok(order);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", "取消订单失败",
                    "message", e.getMessage()
            ));
        }
    }

    @PostMapping("/{orderId}/refund")
    public ResponseEntity<?> refundOrder(@PathVariable Integer orderId) {
        try {
            Order order = orderService.refundOrder(orderId);
            return ResponseEntity.ok(order);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", "退款失败",
                    "message", e.getMessage()
            ));
        }
    }
} 