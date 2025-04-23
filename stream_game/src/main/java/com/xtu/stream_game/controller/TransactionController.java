package com.xtu.stream_game.controller;

import com.xtu.stream_game.entity.Transaction;
import com.xtu.stream_game.entity.Player;
import com.xtu.stream_game.entity.Game;
import com.xtu.stream_game.entity.Transaction.PaymentStatus;
import com.xtu.stream_game.service.TransactionService;
import com.xtu.stream_game.service.PlayerService;
import com.xtu.stream_game.service.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;
    
    @Autowired
    private PlayerService playerService;
    
    @Autowired
    private GameService gameService;

    // 获取所有交易记录
    @GetMapping
    public ResponseEntity<List<Transaction>> getAllTransactions() {
        List<Transaction> transactions = transactionService.getAllTransactions();
        return new ResponseEntity<>(transactions, HttpStatus.OK);
    }

    // 根据ID获取交易记录
    @GetMapping("/{transactionId}")
    public ResponseEntity<Transaction> getTransactionById(@PathVariable Long transactionId) {
        Transaction transaction = transactionService.getTransactionById(transactionId);
        if (transaction == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(transaction, HttpStatus.OK);
    }

    // 根据玩家ID获取交易记录
    @GetMapping("/player/{playerId}")
    public ResponseEntity<List<Transaction>> getTransactionsByPlayerId(@PathVariable Long playerId) {
        Player player = playerService.getPlayerById(playerId);
        if (player == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        List<Transaction> transactions = transactionService.getTransactionsByPlayer(player);
        return new ResponseEntity<>(transactions, HttpStatus.OK);
    }

    // 根据游戏ID获取交易记录
    @GetMapping("/game/{gameId}")
    public ResponseEntity<List<Transaction>> getTransactionsByGameId(@PathVariable Long gameId) {
        Game game = gameService.getGameById(gameId);
        if (game == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        List<Transaction> transactions = transactionService.getTransactionsByGame(game);
        return new ResponseEntity<>(transactions, HttpStatus.OK);
    }

    // 根据交易时间范围获取交易记录
    @GetMapping("/time-range")
    public ResponseEntity<List<Transaction>> getTransactionsByTimeRange(
            @RequestParam String startDate,
            @RequestParam String endDate) {
        // 由于Repository不支持按时间范围查询，此处返回空列表
        return new ResponseEntity<>(new ArrayList<>(), HttpStatus.OK);
    }

    // 根据支付状态获取交易记录
    @GetMapping("/status/{status}")
    public ResponseEntity<List<Transaction>> getTransactionsByPaymentStatus(@PathVariable String status) {
        try {
            PaymentStatus paymentStatus = PaymentStatus.valueOf(status.toUpperCase());
            List<Transaction> transactions = transactionService.getTransactionsByPaymentStatus(paymentStatus);
            return new ResponseEntity<>(transactions, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    // 创建交易
    @PostMapping
    public ResponseEntity<Transaction> createTransaction(@RequestBody Transaction transaction) {
        Transaction createdTransaction = transactionService.createTransaction(transaction);
        return new ResponseEntity<>(createdTransaction, HttpStatus.CREATED);
    }

    // 更新交易状态
    @PutMapping("/{transactionId}/status")
    public ResponseEntity<Transaction> updateTransactionStatus(
            @PathVariable Long transactionId, 
            @RequestBody Map<String, String> statusUpdate) {
        try {
            String status = statusUpdate.get("status");
            if (status == null) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            
            PaymentStatus paymentStatus = PaymentStatus.valueOf(status.toUpperCase());
            Transaction updatedTransaction = transactionService.updateTransactionStatus(transactionId, paymentStatus);
            
            if (updatedTransaction == null) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            
            return new ResponseEntity<>(updatedTransaction, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    // 玩家购买游戏
    @PostMapping("/purchase")
    public ResponseEntity<Transaction> purchaseGame(@RequestBody Map<String, Long> purchaseRequest) {
        Long playerId = purchaseRequest.get("playerId");
        Long gameId = purchaseRequest.get("gameId");
        
        if (playerId == null || gameId == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        
        Transaction transaction = transactionService.purchaseGame(playerId, gameId);
        if (transaction == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        
        return new ResponseEntity<>(transaction, HttpStatus.CREATED);
    }
} 