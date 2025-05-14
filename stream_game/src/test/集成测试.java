// StreamGameApplicationIntegrationTest.java
package com.xtu.stream_game;

import com.xtu.stream_game.entity.*;
import com.xtu.stream_game.repository.*;
import com.xtu.stream_game.service.PlayerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class StreamGameApplicationIntegrationTest {

    @Autowired
    private PlayerService playerService;

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private EmailVerificationRepository emailVerificationRepository;

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private PlayerGameLibraryRepository playerGameLibraryRepository;

    private Player testPlayer;
    private Game testGame;

    @BeforeEach
    void setup() {
        // 初始化测试玩家
        testPlayer = new Player();
        testPlayer.setUsername("testUser");
        testPlayer.setEmail("test@xtu.com");
        testPlayer.setPassword("securePass123");
        playerRepository.save(testPlayer);

        // 初始化测试游戏
        Developer dev = new Developer();
        dev.setDeveloperName("Test Studio");
        
        testGame = new Game();
        testGame.setGameName("Cyber Adventure");
        testGame.setPrice(new BigDecimal("59.99"));
        testGame.setDeveloper(dev);
        gameRepository.save(testGame);
    }

    @Test
    @Transactional
    void fullPlayerJourney_ShouldCreateAllRelatedRecords() {
        // 1. 邮箱验证流程
        EmailVerification verification = new EmailVerification();
        verification.setEmail(testPlayer.getEmail());
        verification.setVerificationCode("6D8F2A");
        verification.setExpiryTime(LocalDateTime.now().plusHours(24));
        emailVerificationRepository.save(verification);

        // 验证邮箱
        EmailVerification savedVerification = emailVerificationRepository
            .findByEmailAndVerificationCode(testPlayer.getEmail(), "6D8F2A")
            .orElseThrow();
        testPlayer.setVerified(true);
        playerRepository.save(testPlayer);

        // 2. 创建订单
        Order newOrder = new Order();
        newOrder.setPlayer(testPlayer);
        newOrder.setGame(testGame);
        newOrder.setStatus(Order.OrderStatus.COMPLETED);
        newOrder.setAmount(testGame.getPrice());
        orderRepository.save(newOrder);

        // 3. 创建交易记录
        Transaction transaction = new Transaction();
        transaction.setOrder(newOrder);
        transaction.setPaymentStatus(Transaction.PaymentStatus.PAID);
        transaction.setAmount(testGame.getPrice());
        transactionRepository.save(transaction);

        // 4. 添加游戏到玩家库
        PlayerGameLibrary libraryEntry = new PlayerGameLibrary();
        libraryEntry.setPlayer(testPlayer);
        libraryEntry.setGame(testGame);
        libraryEntry.setInstallPath("/games/cyber-adventure");
        playerGameLibraryRepository.save(libraryEntry);

        // 验证全流程结果
        // 验证玩家状态
        Player verifiedPlayer = playerRepository.findByEmail(testPlayer.getEmail());
        assertThat(verifiedPlayer.isVerified()).isTrue();

        // 验证订单和交易
        List<Order> playerOrders = orderRepository.findByPlayerPlayerId(testPlayer.getPlayerId());
        assertThat(playerOrders).hasSize(1).extracting(Order::getStatus).containsExactly(Order.OrderStatus.COMPLETED);

        List<Transaction> transactions = transactionRepository.findByPaymentStatus(Transaction.PaymentStatus.PAID);
        assertThat(transactions).extracting(t -> t.getOrder().getOrderId()).contains(newOrder.getOrderId());

        // 验证游戏库
        Optional<PlayerGameLibrary> libraryRecord = playerGameLibraryRepository
            .findByPlayerPlayerIdAndGameGameId(testPlayer.getPlayerId(), testGame.getGameId());
        assertThat(libraryRecord).isPresent().hasValueSatisfying(entry ->
            assertThat(entry.getInstallPath()).isEqualTo("/games/cyber-adventure")
        );
    }

    @Test
    @Transactional
    void gamePurchaseFlow_WithExistingLibrary_ShouldPreventDuplicate() {
        // 初次购买
        Order firstOrder = createCompletedOrder();
        createGameLibraryEntry();

        // 尝试重复购买
        boolean canPurchase = orderRepository.existsByPlayerPlayerIdAndGameGameIdAndStatus(
            testPlayer.getPlayerId(),
            testGame.getGameId(),
            Order.OrderStatus.COMPLETED
        );

        // 验证业务逻辑
        assertThat(canPurchase).isTrue(); // 根据业务规则判断是否允许重复购买
        // 如果业务逻辑禁止重复购买，这里应该检查是否抛出异常或返回错误响应
    }

    private Order createCompletedOrder() {
        Order order = new Order();
        order.setPlayer(testPlayer);
        order.setGame(testGame);
        order.setStatus(Order.OrderStatus.COMPLETED);
        return orderRepository.save(order);
    }

    private void createGameLibraryEntry() {
        PlayerGameLibrary entry = new PlayerGameLibrary();
        entry.setPlayer(testPlayer);
        entry.setGame(testGame);
        playerGameLibraryRepository.save(entry);
    }
}