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
    public ResponseEntity<Transaction> getTransactionById(@PathVariable Integer transactionId) {
        Transaction transaction = transactionService.getTransactionById(transactionId);
        if (transaction == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(transaction, HttpStatus.OK);
    }

    // 根据玩家ID获取交易记录
    @GetMapping("/player/{playerId}")
    public ResponseEntity<List<Transaction>> getTransactionsByPlayerId(@PathVariable Integer playerId) {
        Player player = playerService.getPlayerById(playerId);
        if (player == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        List<Transaction> transactions = transactionService.getTransactionsByPlayer(player);
        return new ResponseEntity<>(transactions, HttpStatus.OK);
    }

    // 根据游戏ID获取交易记录
    @GetMapping("/game/{gameId}")
    public ResponseEntity<List<Transaction>> getTransactionsByGameId(@PathVariable Integer gameId) {
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
            @PathVariable Integer transactionId, 
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
    public ResponseEntity<Transaction> purchaseGame(@RequestBody Map<String, Integer> purchaseRequest) {
        Integer playerId = purchaseRequest.get("playerId");
        Integer gameId = purchaseRequest.get("gameId");
        
        if (playerId == null || gameId == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        
        Transaction transaction = transactionService.purchaseGame(playerId, gameId);
        if (transaction == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        
        return new ResponseEntity<>(transaction, HttpStatus.CREATED);
    }
    
    // 批量购买游戏（从购物车）
    @PostMapping("/purchase-cart")
    public ResponseEntity<?> purchaseCart(@RequestBody Map<String, Object> purchaseRequest) {
        try {
            Integer playerId = (Integer) purchaseRequest.get("playerId");
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> cartItems = (List<Map<String, Object>>) purchaseRequest.get("items");
            
            if (playerId == null || cartItems == null || cartItems.isEmpty()) {
                return new ResponseEntity<>(Map.of(
                    "success", false,
                    "message", "无效的请求数据：需要playerId和购物车商品列表"
                ), HttpStatus.BAD_REQUEST);
            }
            
            // 处理结果统计
            int successCount = 0;
            int failCount = 0;
            List<String> errorMessages = new ArrayList<>();
            List<Transaction> successTransactions = new ArrayList<>();
            
            // 遍历购物车商品
            for (Map<String, Object> item : cartItems) {
                String title = (String) item.get("title");
                try {
                    // 根据游戏名称查询游戏
                    Game game = gameService.getGameByName(title);
                    if (game == null) {
                        // 尝试进行模糊匹配
                        List<Game> games = gameService.getGamesByType("");  // 获取所有游戏
                        for (Game g : games) {
                            String gameName = g.getGameName().toLowerCase();
                            String itemTitle = title.toLowerCase();
                            if (gameName.equals(itemTitle) || 
                                gameName.contains(itemTitle) || 
                                itemTitle.contains(gameName)) {
                                game = g;
                                break;
                            }
                        }
                        
                        if (game == null) {
                            failCount++;
                            errorMessages.add("找不到游戏：" + title);
                            continue;
                        }
                    }
                    
                    // 获取游戏ID并调用购买方法
                    Integer gameId = game.getGameId();
                    Transaction transaction = transactionService.purchaseGame(playerId, gameId);
                    
                    if (transaction != null) {
                        successCount++;
                        successTransactions.add(transaction);
                    } else {
                        failCount++;
                        errorMessages.add("购买失败：" + title);
                    }
                } catch (Exception e) {
                    failCount++;
                    errorMessages.add("处理游戏[" + title + "]时出错: " + e.getMessage());
                }
            }
            
            // 返回处理结果
            return new ResponseEntity<>(Map.of(
                "success", failCount == 0,
                "message", String.format("%d个游戏购买成功，%d个失败", successCount, failCount),
                "successCount", successCount,
                "failCount", failCount,
                "errors", errorMessages,
                "transactions", successTransactions
            ), HttpStatus.OK);
            
        } catch (Exception e) {
            return new ResponseEntity<>(Map.of(
                "success", false,
                "message", "处理购物车时出错: " + e.getMessage()
            ), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
} 