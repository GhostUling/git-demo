package com.xtu.stream_game.controller;

import com.xtu.stream_game.entity.Order;
import com.xtu.stream_game.service.OrderService;
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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("订单控制器单元测试")
class OrderControllerTest {

    private MockMvc mockMvc;

    @Mock
    private OrderService orderService;

    @InjectMocks
    private OrderController orderController;

    private Order testOrder;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(orderController).build();
        testOrder = new Order(1, 1001, 2001, "购买游戏DLC");
        testOrder.setStatus(Order.OrderStatus.PENDING);
    }

    //----------------------- 创建订单 ------------------------
    @Test
    @DisplayName("创建订单 - 成功")
    void createOrder_Success() throws Exception {
        // Given
        when(orderService.createOrder(1001, 2001, "购买游戏DLC")).thenReturn(testOrder);

        // When & Then
        mockMvc.perform(post("/api/orders")
                .param("playerId", "1001")
                .param("gameId", "2001")
                .param("description", "购买游戏DLC"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description", is("购买游戏DLC")));
    }

    @Test
    @DisplayName("创建订单 - 缺少必要参数")
    void createOrder_MissingParams() throws Exception {
        mockMvc.perform(post("/api/orders")
                .param("playerId", "1001")) // 缺少gameId
                .andExpect(status().isBadRequest());
    }

    //----------------------- 获取订单详情 ------------------------
    @Test
    @DisplayName("获取订单详情 - 存在")
    void getOrder_Exists() throws Exception {
        when(orderService.getOrderById(1)).thenReturn(testOrder);

        mockMvc.perform(get("/api/orders/1"))
                .andExpect(jsonPath("$.id", is(1)));
    }

    @Test
    @DisplayName("获取订单详情 - 不存在")
    void getOrder_NotExists() throws Exception {
        when(orderService.getOrderById(999)).thenThrow(new RuntimeException("订单不存在"));

        mockMvc.perform(get("/api/orders/999"))
                .andExpect(jsonPath("$.error", is("获取订单失败")));
    }

    //----------------------- 获取玩家订单 ------------------------
    @Test
    @DisplayName("获取玩家订单 - 有数据")
    void getPlayerOrders_WithData() throws Exception {
        List<Order> orders = Arrays.asList(testOrder, new Order(2, 1001, 2002, "购买皮肤"));
        when(orderService.getPlayerOrders(1001)).thenReturn(orders);

        mockMvc.perform(get("/api/orders/player/1001"))
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    @DisplayName("获取玩家订单 - 空列表")
    void getPlayerOrders_Empty() throws Exception {
        when(orderService.getPlayerOrders(999)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/orders/player/999"))
                .andExpect(jsonPath("$", empty()));
    }

    //----------------------- 按状态筛选玩家订单 ------------------------
    @Test
    @DisplayName("获取玩家状态订单 - 成功")
    void getPlayerOrdersByStatus_Success() throws Exception {
        List<Order> orders = Collections.singletonList(testOrder);
        when(orderService.getPlayerOrdersByStatus(1001, Order.OrderStatus.PENDING)).thenReturn(orders);

        mockMvc.perform(get("/api/orders/player/1001/status/PENDING"))
                .andExpect(jsonPath("$[0].status", is("PENDING")));
    }

    //----------------------- 获取全局状态订单 ------------------------
    @Test
    @DisplayName("获取所有状态订单 - 成功")
    void getAllOrdersByStatus_Success() throws Exception {
        List<Order> orders = Arrays.asList(testOrder, new Order(2, 1002, 2003, "待处理订单"));
        when(orderService.getAllOrdersByStatus(Order.OrderStatus.PENDING)).thenReturn(orders);

        mockMvc.perform(get("/api/orders/status/PENDING"))
                .andExpect(jsonPath("$", hasSize(2)));
    }

    //----------------------- 更新订单状态 ------------------------
    @Test
    @DisplayName("更新订单状态 - 成功")
    void updateOrderStatus_Success() throws Exception {
        Order updatedOrder = testOrder;
        updatedOrder.setStatus(Order.OrderStatus.COMPLETED);
        updatedOrder.setTransactionId("TX123456");

        when(orderService.updateOrderStatus(1, Order.OrderStatus.COMPLETED, "TX123456"))
                .thenReturn(updatedOrder);

        mockMvc.perform(post("/api/orders/1/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"status\":\"COMPLETED\",\"transactionId\":\"TX123456\"}"))
                .andExpect(jsonPath("$.transactionId", is("TX123456")));
    }

    @Test
    @DisplayName("更新订单状态 - 无效状态")
    void updateOrderStatus_InvalidStatus() throws Exception {
        mockMvc.perform(post("/api/orders/1/status")
                .content("{\"status\":\"INVALID_STATUS\"}")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error", is("更新订单状态失败")));
    }

    //----------------------- 取消订单 ------------------------
    @Test
    @DisplayName("取消订单 - 成功")
    void cancelOrder_Success() throws Exception {
        Order canceledOrder = testOrder;
        canceledOrder.setStatus(Order.OrderStatus.CANCELLED);
        when(orderService.cancelOrder(1)).thenReturn(canceledOrder);

        mockMvc.perform(post("/api/orders/1/cancel"))
                .andExpect(jsonPath("$.status", is("CANCELLED")));
    }

    @Test
    @DisplayName("取消订单 - 订单不存在")
    void cancelOrder_NotFound() throws Exception {
        when(orderService.cancelOrder(999)).thenThrow(new RuntimeException("订单不存在"));

        mockMvc.perform(post("/api/orders/999/cancel"))
                .andExpect(jsonPath("$.error", is("取消订单失败")));
    }

    //----------------------- 退款订单 ------------------------
    @Test
    @DisplayName("退款订单 - 成功")
    void refundOrder_Success() throws Exception {
        Order refundedOrder = testOrder;
        refundedOrder.setStatus(Order.OrderStatus.REFUNDED);
        when(orderService.refundOrder(1)).thenReturn(refundedOrder);

        mockMvc.perform(post("/api/orders/1/refund"))
                .andExpect(jsonPath("$.status", is("REFUNDED")));
    }

    @Test
    @DisplayName("退款订单 - 不可退款状态")
    void refundOrder_InvalidStatus() throws Exception {
        when(orderService.refundOrder(1))
                .thenThrow(new RuntimeException("当前状态不可退款"));

        mockMvc.perform(post("/api/orders/1/refund"))
                .andExpect(jsonPath("$.message", containsString("不可退款")));
    }
}