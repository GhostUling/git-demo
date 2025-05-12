package com.xtu.stream_game.repository;

import com.xtu.stream_game.entity.Transaction;
import com.xtu.stream_game.entity.Game;
import com.xtu.stream_game.entity.Player;
import com.xtu.stream_game.entity.Transaction.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Integer> {
    // 根据玩家查找交易记录
    List<Transaction> findByPlayer(Player player);
    
    // 根据游戏查找交易记录
    List<Transaction> findByGame(Game game);
    
    // 根据支付状态查找交易记录
    List<Transaction> findByPaymentStatus(PaymentStatus paymentStatus);
} 