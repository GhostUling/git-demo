package com.xtu.stream_game.service.impl;

import com.xtu.stream_game.entity.Transaction;
import com.xtu.stream_game.entity.Player;
import com.xtu.stream_game.entity.Game;
import com.xtu.stream_game.entity.Transaction.PaymentStatus;
import com.xtu.stream_game.repository.TransactionRepository;
import com.xtu.stream_game.repository.PlayerRepository;
import com.xtu.stream_game.repository.GameRepository;
import com.xtu.stream_game.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class TransactionServiceImpl implements TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;
    
    @Autowired
    private PlayerRepository playerRepository;
    
    @Autowired
    private GameRepository gameRepository;

    @Override
    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }

    @Override
    public Transaction getTransactionById(Integer transactionId) {
        return transactionRepository.findById(transactionId).orElse(null);
    }

    @Override
    public List<Transaction> getTransactionsByPlayer(Player player) {
        return transactionRepository.findByPlayer(player);
    }

    @Override
    public List<Transaction> getTransactionsByGame(Game game) {
        return transactionRepository.findByGame(game);
    }

    @Override
    public List<Transaction> getTransactionsByPaymentStatus(PaymentStatus paymentStatus) {
        return transactionRepository.findByPaymentStatus(paymentStatus);
    }

    @Override
    public Transaction createTransaction(Transaction transaction) {
        // 设置交易时间为当前时间
        transaction.setTransactionTime(new Date());
        
        // 如果没有设置支付状态，默认为UNPAID
        if (transaction.getPaymentStatus() == null) {
            transaction.setPaymentStatus(PaymentStatus.UNPAID);
        }
        
        return transactionRepository.save(transaction);
    }

    @Override
    public Transaction updateTransactionStatus(Integer transactionId, PaymentStatus paymentStatus) {
        Optional<Transaction> transactionOpt = transactionRepository.findById(transactionId);
        
        if (transactionOpt.isPresent()) {
            Transaction transaction = transactionOpt.get();
            transaction.setPaymentStatus(paymentStatus);
            return transactionRepository.save(transaction);
        }
        
        return null;
    }

    @Override
    public Transaction purchaseGame(Integer playerId, Integer gameId) {
        // 获取玩家和游戏
        Optional<Player> playerOpt = playerRepository.findById(playerId);
        Optional<Game> gameOpt = gameRepository.findById(gameId);
        
        if (playerOpt.isPresent() && gameOpt.isPresent()) {
            Player player = playerOpt.get();
            Game game = gameOpt.get();
            
            // 创建交易记录
            Transaction transaction = new Transaction();
            transaction.setPlayer(player);
            transaction.setGame(game);
            transaction.setAmount(game.getPrice());
            transaction.setTransactionTime(new Date());
            transaction.setPaymentStatus(PaymentStatus.PAID); // 假设直接支付成功
            
            return transactionRepository.save(transaction);
        }
        
        return null;
    }
} 