package com.xtu.stream_game.repository;

import com.xtu.stream_game.entity.Developer;
import com.xtu.stream_game.entity.Game;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class GameRepositoryTest {

    @Autowired
    private TestEntityManager em;

    @Autowired
    private GameRepository repo;

    @Test
    void findByGameName_ShouldReturnGame() {
        Game game = new Game();
        game.setGameName("CyberQuest");
        em.persist(game);
        em.flush();

        Game found = repo.findByGameName("CyberQuest");
        assertThat(found).isEqualTo(game);
    }

    @Test
    void findByDeveloperDeveloperId_ShouldReturnGames() {
        Developer dev = new Developer();
        em.persist(dev);

        Game game = new Game();
        game.setDeveloper(dev);
        em.persist(game);
        em.flush();

        List<Game> results = repo.findByDeveloperDeveloperId(dev.getDeveloperId());
        assertThat(results).hasSize(1).contains(game);
    }
}