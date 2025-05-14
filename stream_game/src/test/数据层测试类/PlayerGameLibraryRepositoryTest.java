package com.xtu.stream_game.repository;

import com.xtu.stream_game.entity.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class PlayerGameLibraryRepositoryTest {

    @Autowired
    private TestEntityManager em;

    @Autowired
    private PlayerGameLibraryRepository repo;

    @Test
    void findByPlayerIdAndGameTitleContainingIgnoreCase_ShouldMatch() {
        Player player = new Player();
        Game game = new Game();
        game.setGameName("Space Adventure");
        PlayerGameLibrary pgl = new PlayerGameLibrary();
        pgl.setPlayer(player);
        pgl.setGame(game);
        em.persist(player);
        em.persist(game);
        em.persist(pgl);
        em.flush();

        List<PlayerGameLibrary> results = repo.findByPlayerIdAndGameTitleContainingIgnoreCase(
            player.getPlayerId(),
            "adventure"
        );
        assertThat(results).hasSize(1);
    }
}