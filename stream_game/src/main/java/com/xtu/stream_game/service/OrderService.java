package com.xtu.stream_game.service;

import com.xtu.stream_game.entity.Order;
import java.util.List;

public interface OrderService {
    Order createOrder(Long playerId, Long gameId, String description);
    Order getOrderById(Long orderId);
    List<Order> getPlayerOrders(Long playerId);
    List<Order> getPlayerOrdersByStatus(Long playerId, Order.OrderStatus status);
    List<Order> getAllOrdersByStatus(Order.OrderStatus status);
    Order updateOrderStatus(Long orderId, Order.OrderStatus status, String transactionId);
    Order cancelOrder(Long orderId);
    Order refundOrder(Long orderId);
} 