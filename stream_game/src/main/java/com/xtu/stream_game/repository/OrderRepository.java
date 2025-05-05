package com.xtu.stream_game.repository;

import com.xtu.stream_game.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByPlayerId(Long playerId);
    List<Order> findByPlayerIdAndStatus(Long playerId, Order.OrderStatus status);
    List<Order> findByStatus(Order.OrderStatus status);
    List<Order> findByGameId(Long gameId);
    boolean existsByPlayerIdAndGameIdAndStatus(Long playerId, Long gameId, Order.OrderStatus status);
} 