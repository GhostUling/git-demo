package com.xtu.stream_game.service;

import com.xtu.stream_game.entity.Transaction;
import com.xtu.stream_game.repository.TransactionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private TransactionServiceImpl transactionService;

    @Test
    void createTransaction_ShouldSetDefaultStatus() {
        Transaction transaction = new Transaction();
        when(transactionRepository.save(any())).thenReturn(transaction);

        Transaction result = transactionService.createTransaction(transaction);
        assertNotNull(result.getPaymentStatus());
    }
}