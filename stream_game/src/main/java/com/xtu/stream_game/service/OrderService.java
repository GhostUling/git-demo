package com.xtu.stream_game.service;

import com.xtu.stream_game.entity.Order;
import java.util.List;

public interface OrderService {
    Order createOrder(Integer playerId, Integer gameId, String description);
    Order getOrderById(Integer orderId);
    List<Order> getPlayerOrders(Integer playerId);
    List<Order> getPlayerOrdersByStatus(Integer playerId, Order.OrderStatus status);
    List<Order> getAllOrdersByStatus(Order.OrderStatus status);
    Order updateOrderStatus(Integer orderId, Order.OrderStatus status, String transactionId);
    Order cancelOrder(Integer orderId);
    Order refundOrder(Integer orderId);
} 