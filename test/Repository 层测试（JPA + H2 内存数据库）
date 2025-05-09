package com.stream.game.repository;

import com.stream.game.entity.Game;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import static org.assertj.core.api.Assertions.*;

@DataJpaTest
public class GameRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private GameRepository gameRepository;

    @Test
    void testFindByName() {
        Game game = new Game("Half-Life 3");
        entityManager.persist(game);
        entityManager.flush();

        Game found = gameRepository.findByName("Half-Life 3");
        assertThat(found.getName()).isEqualTo(game.getName());
    }
}