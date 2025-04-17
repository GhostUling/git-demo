package com.xtu.stream_game.repository;

import com.xtu.stream_game.entity.Transaction;
import com.xtu.stream_game.entity.Game;
import com.xtu.stream_game.entity.Player;
import com.xtu.stream_game.entity.Transaction.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Date;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Integer> {
    // 根据玩家查找交易记录
    List<Transaction> findByPlayer(Player player);
    
    // 根据游戏查找交易记录
    List<Transaction> findByGame(Game game);
    
    // 根据交易时间范围查找交易记录
    List<Transaction> findByTransactionTimeBetween(Date startDate, Date endDate);
    
    // 根据支付状态查找交易记录
    List<Transaction> findByPaymentStatus(PaymentStatus paymentStatus);
    
    // 根据玩家和支付状态查找交易记录
    List<Transaction> findByPlayerAndPaymentStatus(Player player, PaymentStatus paymentStatus);
    
    // 根据游戏和支付状态查找交易记录
    List<Transaction> findByGameAndPaymentStatus(Game game, PaymentStatus paymentStatus);
} 