package com.xtu.stream_game.repository;

import com.xtu.stream_game.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {
    List<Order> findByPlayerPlayerId(Integer playerId);
    List<Order> findByPlayerPlayerIdAndStatus(Integer playerId, Order.OrderStatus status);
    List<Order> findByStatus(Order.OrderStatus status);
    List<Order> findByGameGameId(Integer gameId);
    boolean existsByPlayerPlayerIdAndGameGameIdAndStatus(Integer playerId, Integer gameId, Order.OrderStatus status);
} 