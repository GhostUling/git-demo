package com.xtu.stream_game.repository;

import com.xtu.stream_game.entity.Player;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class PlayerRepositoryTest {

    @Autowired
    private TestEntityManager em;

    @Autowired
    private PlayerRepository repo;

    @Test
    void findByUsername_ShouldReturnPlayer() {
        Player player = new Player();
        player.setUsername("gamer123");
        em.persist(player);
        em.flush();

        Player found = repo.findByUsername("gamer123");
        assertThat(found).isEqualTo(player);
    }
}