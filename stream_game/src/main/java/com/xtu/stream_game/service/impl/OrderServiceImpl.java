package com.xtu.stream_game.service.impl;

import com.xtu.stream_game.entity.*;
import com.xtu.stream_game.repository.OrderRepository;
import com.xtu.stream_game.repository.PlayerRepository;
import com.xtu.stream_game.repository.GameRepository;
import com.xtu.stream_game.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private GameRepository gameRepository;

    @Override
    @Transactional
    public Order createOrder(Long playerId, Long gameId, String description) {
        Player player = playerRepository.findById(playerId)
                .orElseThrow(() -> new RuntimeException("玩家不存在"));
        
        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new RuntimeException("游戏不存在"));

        // 检查玩家是否已经购买过该游戏
        if (orderRepository.existsByPlayerIdAndGameIdAndStatus(playerId, gameId, Order.OrderStatus.PAID)) {
            throw new RuntimeException("您已经购买过该游戏");
        }

        Order order = new Order();
        order.setPlayer(player);
        order.setGame(game);
        order.setAmount(game.getPrice());
        order.setStatus(Order.OrderStatus.PENDING);
        order.setCreateTime(LocalDateTime.now());
        order.setDescription(description);

        return orderRepository.save(order);
    }

    @Override
    public Order getOrderById(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("订单不存在"));
    }

    @Override
    public List<Order> getPlayerOrders(Long playerId) {
        return orderRepository.findByPlayerId(playerId);
    }

    @Override
    public List<Order> getPlayerOrdersByStatus(Long playerId, Order.OrderStatus status) {
        return orderRepository.findByPlayerIdAndStatus(playerId, status);
    }

    @Override
    public List<Order> getAllOrdersByStatus(Order.OrderStatus status) {
        return orderRepository.findByStatus(status);
    }

    @Override
    @Transactional
    public Order updateOrderStatus(Long orderId, Order.OrderStatus status, String transactionId) {
        Order order = getOrderById(orderId);
        
        if (status == Order.OrderStatus.PAID) {
            order.setPayTime(LocalDateTime.now());
            order.setTransactionId(transactionId);
        }
        
        order.setStatus(status);
        return orderRepository.save(order);
    }

    @Override
    @Transactional
    public Order cancelOrder(Long orderId) {
        Order order = getOrderById(orderId);
        
        if (order.getStatus() != Order.OrderStatus.PENDING) {
            throw new RuntimeException("只有待支付的订单可以取消");
        }
        
        order.setStatus(Order.OrderStatus.CANCELLED);
        return orderRepository.save(order);
    }

    @Override
    @Transactional
    public Order refundOrder(Long orderId) {
        Order order = getOrderById(orderId);
        
        if (order.getStatus() != Order.OrderStatus.PAID) {
            throw new RuntimeException("只有已支付的订单可以退款");
        }
        
        order.setStatus(Order.OrderStatus.REFUNDED);
        return orderRepository.save(order);
    }
} 