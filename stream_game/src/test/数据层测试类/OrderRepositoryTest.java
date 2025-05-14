package com.xtu.stream_game.repository;

import com.xtu.stream_game.entity.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class OrderRepositoryTest {

    @Autowired
    private TestEntityManager em;

    @Autowired
    private OrderRepository repo;

    @Test
    void existsByPlayerPlayerIdAndGameGameIdAndStatus_ShouldReturnTrue() {
        Player player = new Player();
        Game game = new Game();
        Order order = new Order();
        order.setPlayer(player);
        order.setGame(game);
        order.setStatus(Order.OrderStatus.COMPLETED);
        em.persist(player);
        em.persist(game);
        em.persist(order);
        em.flush();

        boolean exists = repo.existsByPlayerPlayerIdAndGameGameIdAndStatus(
            player.getPlayerId(),
            game.getGameId(),
            Order.OrderStatus.COMPLETED
        );
        assertThat(exists).isTrue();
    }
}
