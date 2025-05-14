package com.xtu.stream_game.service;

import com.xtu.stream_game.entity.Order;
import com.xtu.stream_game.entity.Player;
import com.xtu.stream_game.repository.OrderRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderServiceImpl orderService;

    @Test
    void createOrder_ShouldPersistOrder() {
        Order order = new Order();
        when(orderRepository.save(any())).thenReturn(order);

        Order result = orderService.createOrder(1, 1, "Test");
        assertNotNull(result);
        verify(orderRepository).save(any());
    }
}