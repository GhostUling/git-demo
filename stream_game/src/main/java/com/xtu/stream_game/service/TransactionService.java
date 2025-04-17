package com.xtu.stream_game.service;

import com.xtu.stream_game.entity.Transaction;
import com.xtu.stream_game.entity.Player;
import com.xtu.stream_game.entity.Game;
import com.xtu.stream_game.entity.Transaction.PaymentStatus;
import java.util.List;

public interface TransactionService {
    // 获取所有交易记录
    List<Transaction> getAllTransactions();
    
    // 根据ID获取交易记录
    Transaction getTransactionById(int transactionId);
    
    // 根据玩家获取交易记录
    List<Transaction> getTransactionsByPlayer(Player player);
    
    // 根据游戏获取交易记录
    List<Transaction> getTransactionsByGame(Game game);
    
    // 根据支付状态获取交易记录
    List<Transaction> getTransactionsByPaymentStatus(PaymentStatus paymentStatus);
    
    // 创建交易
    Transaction createTransaction(Transaction transaction);
    
    // 更新交易状态
    Transaction updateTransactionStatus(int transactionId, PaymentStatus paymentStatus);
    
    // 玩家购买游戏
    Transaction purchaseGame(int playerId, int gameId);
} 