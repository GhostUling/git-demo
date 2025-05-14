package com.xtu.stream_game.repository;

import com.xtu.stream_game.entity.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class TransactionRepositoryTest {

    @Autowired
    private TestEntityManager em;

    @Autowired
    private TransactionRepository repo;

    @Test
    void findByPaymentStatus_ShouldFilterTransactions() {
        Transaction t = new Transaction();
        t.setPaymentStatus(Transaction.PaymentStatus.PAID);
        em.persist(t);
        em.flush();

        List<Transaction> results = repo.findByPaymentStatus(Transaction.PaymentStatus.PAID);
        assertThat(results).hasSize(1).contains(t);
    }
}