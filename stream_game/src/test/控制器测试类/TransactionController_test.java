package com.xtu.stream_game.controller;

import com.xtu.stream_game.entity.*;
import com.xtu.stream_game.service.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.*;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("交易控制器单元测试")
class TransactionControllerTest {

    private MockMvc mockMvc;

    @Mock
    private TransactionService transactionService;

    @Mock
    private PlayerService playerService;

    @Mock
    private GameService gameService;

    @InjectMocks
    private TransactionController transactionController;

    private Transaction testTransaction;
    private Player testPlayer;
    private Game testGame;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(transactionController).build();
        
        testPlayer = new Player(1, "testPlayer", "password", "test@example.com");
        testGame = new Game(100, "Test Game", "RPG", 59.99, 1);
        testTransaction = new Transaction(
            1,
            testPlayer,
            testGame,
            59.99,
            new Date(),
            Transaction.PaymentStatus.COMPLETED
        );
    }

    //----------------------- 获取交易记录测试 ------------------------
    @Test
    @DisplayName("获取所有交易记录 - 成功")
    void getAllTransactions_Success() throws Exception {
        when(transactionService.getAllTransactions()).thenReturn(Collections.singletonList(testTransaction));

        mockMvc.perform(get("/api/transactions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].amount", is(59.99)));
    }

    @Test
    @DisplayName("根据ID获取交易记录 - 存在")
    void getTransactionById_Exists() throws Exception {
        when(transactionService.getTransactionById(1)).thenReturn(testTransaction);

        mockMvc.perform(get("/api/transactions/1"))
                .andExpect(jsonPath("$.paymentStatus", is("COMPLETED")));
    }

    @Test
    @DisplayName("根据ID获取交易记录 - 不存在")
    void getTransactionById_NotExists() throws Exception {
        when(transactionService.getTransactionById(999)).thenReturn(null);

        mockMvc.perform(get("/api/transactions/999"))
                .andExpect(status().isNotFound());
    }

    //----------------------- 玩家相关交易查询测试 ------------------------
    @Test
    @DisplayName("获取玩家交易记录 - 玩家存在")
    void getTransactionsByPlayerId_PlayerExists() throws Exception {
        when(playerService.getPlayerById(1)).thenReturn(testPlayer);
        when(transactionService.getTransactionsByPlayer(testPlayer))
                .thenReturn(Collections.singletonList(testTransaction));

        mockMvc.perform(get("/api/transactions/player/1"))
                .andExpect(jsonPath("$[0].player.username", is("testPlayer")));
    }

    @Test
    @DisplayName("获取玩家交易记录 - 玩家不存在")
    void getTransactionsByPlayerId_PlayerNotExists() throws Exception {
        when(playerService.getPlayerById(999)).thenReturn(null);

        mockMvc.perform(get("/api/transactions/player/999"))
                .andExpect(status().isNotFound());
    }

    //----------------------- 游戏相关交易查询测试 ------------------------
    @Test
    @DisplayName("获取游戏交易记录 - 游戏存在")
    void getTransactionsByGameId_GameExists() throws Exception {
        when(gameService.getGameById(100)).thenReturn(testGame);
        when(transactionService.getTransactionsByGame(testGame))
                .thenReturn(Collections.singletonList(testTransaction));

        mockMvc.perform(get("/api/transactions/game/100"))
                .andExpect(jsonPath("$[0].game.name", is("Test Game")));
    }

    @Test
    @DisplayName("获取游戏交易记录 - 游戏不存在")
    void getTransactionsByGameId_GameNotExists() throws Exception {
        when(gameService.getGameById(999)).thenReturn(null);

        mockMvc.perform(get("/api/transactions/game/999"))
                .andExpect(status().isNotFound());
    }

    //----------------------- 状态查询测试 ------------------------
    @Test
    @DisplayName("按支付状态查询 - 有效状态")
    void getTransactionsByPaymentStatus_Valid() throws Exception {
        when(transactionService.getTransactionsByPaymentStatus(Transaction.PaymentStatus.COMPLETED))
                .thenReturn(Collections.singletonList(testTransaction));

        mockMvc.perform(get("/api/transactions/status/completed"))
                .andExpect(jsonPath("$[0].paymentStatus", is("COMPLETED")));
    }

    @Test
    @DisplayName("按支付状态查询 - 无效状态")
    void getTransactionsByPaymentStatus_Invalid() throws Exception {
        mockMvc.perform(get("/api/transactions/status/invalid_status"))
                .andExpect(status().isBadRequest());
    }

    //----------------------- 创建交易测试 ------------------------
    @Test
    @DisplayName("创建交易 - 成功")
    void createTransaction_Success() throws Exception {
        when(transactionService.createTransaction(any())).thenReturn(testTransaction);

        mockMvc.perform(post("/api/transactions")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{ \"amount\": 59.99 }"))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"));
    }

    //----------------------- 更新状态测试 ------------------------
    @Test
    @DisplayName("更新交易状态 - 成功")
    void updateTransactionStatus_Success() throws Exception {
        Transaction updated = testTransaction;
        updated.setPaymentStatus(Transaction.PaymentStatus.REFUNDED);
        
        when(transactionService.updateTransactionStatus(1, Transaction.PaymentStatus.REFUNDED))
                .thenReturn(updated);

        mockMvc.perform(put("/api/transactions/1/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{ \"status\": \"REFUNDED\" }"))
                .andExpect(jsonPath("$.paymentStatus", is("REFUNDED")));
    }

    @Test
    @DisplayName("更新交易状态 - 无效状态")
    void updateTransactionStatus_InvalidStatus() throws Exception {
        mockMvc.perform(put("/api/transactions/1/status")
                .content("{ \"status\": \"INVALID\" }")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    //----------------------- 购买游戏测试 ------------------------
    @Test
    @DisplayName("购买游戏 - 成功")
    void purchaseGame_Success() throws Exception {
        when(transactionService.purchaseGame(1, 100)).thenReturn(testTransaction);
        when(playerService.getPlayerById(1)).thenReturn(testPlayer);
        when(gameService.getGameById(100)).thenReturn(testGame);

        mockMvc.perform(post("/api/transactions/purchase")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{ \"playerId\": 1, \"gameId\": 100 }"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.amount", is(59.99)));
    }

    @Test
    @DisplayName("购买游戏 - 缺少参数")
    void purchaseGame_MissingParams() throws Exception {
        mockMvc.perform(post("/api/transactions/purchase")
                .content("{ \"playerId\": 1 }")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("购买游戏 - 玩家不存在")
    void purchaseGame_PlayerNotFound() throws Exception {
        when(playerService.getPlayerById(999)).thenReturn(null);

        mockMvc.perform(post("/api/transactions/purchase")
                .content("{ \"playerId\": 999, \"gameId\": 100 }")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}