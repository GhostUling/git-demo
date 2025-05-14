package com.xtu.stream_game.repository;

import com.xtu.stream_game.entity.GameUpload;
import com.xtu.stream_game.entity.Developer;
import com.xtu.stream_game.entity.GameUpload.UploadStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class GameUploadRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private GameUploadRepository gameUploadRepository;

    @Test
    void findByDeveloperDeveloperId_ShouldReturnGamesForSpecificDeveloper() {
        // 准备数据
        Developer developer = new Developer();
        entityManager.persist(developer);

        GameUpload game1 = new GameUpload();
        game1.setDeveloper(developer);
        entityManager.persist(game1);

        GameUpload game2 = new GameUpload();
        game2.setDeveloper(developer);
        entityManager.persist(game2);

        entityManager.flush();

        // 执行查询
        List<GameUpload> foundGames = gameUploadRepository.findByDeveloperDeveloperId(developer.getDeveloperId());

        // 验证结果
        assertThat(foundGames).hasSize(2).containsExactlyInAnyOrder(game1, game2);
    }

    @Test
    void findByStatus_ShouldReturnGamesWithSpecificStatus() {
        // 准备数据
        GameUpload pendingGame = new GameUpload();
        pendingGame.setStatus(UploadStatus.PENDING);
        entityManager.persist(pendingGame);

        GameUpload approvedGame = new GameUpload();
        approvedGame.setStatus(UploadStatus.APPROVED);
        entityManager.persist(approvedGame);

        entityManager.flush();

        // 执行查询
        List<GameUpload> pendingGames = gameUploadRepository.findByStatus(UploadStatus.PENDING);

        // 验证结果
        assertThat(pendingGames).hasSize(1).containsExactly(pendingGame);
    }

    @Test
    void findByDeveloperDeveloperIdAndStatus_ShouldReturnFilteredGames() {
        // 准备数据
        Developer dev1 = new Developer();
        entityManager.persist(dev1);

        Developer dev2 = new Developer();
        entityManager.persist(dev2);

        GameUpload game1 = new GameUpload();
        game1.setDeveloper(dev1);
        game1.setStatus(UploadStatus.PENDING);
        entityManager.persist(game1);

        GameUpload game2 = new GameUpload();
        game2.setDeveloper(dev1);
        game2.setStatus(UploadStatus.APPROVED);
        entityManager.persist(game2);

        GameUpload game3 = new GameUpload();
        game3.setDeveloper(dev2);
        game3.setStatus(UploadStatus.PENDING);
        entityManager.persist(game3);

        entityManager.flush();

        // 执行查询
        List<GameUpload> results = gameUploadRepository.findByDeveloperDeveloperIdAndStatus(
            dev1.getDeveloperId(), 
            UploadStatus.PENDING
        );

        // 验证结果
        assertThat(results).hasSize(1).containsExactly(game1);
    }

    @Test
    void findByDeveloperDeveloperId_WhenNoGames_ShouldReturnEmptyList() {
        Developer developer = new Developer();
        entityManager.persist(developer);
        entityManager.flush();

        List<GameUpload> results = gameUploadRepository.findByDeveloperDeveloperId(developer.getDeveloperId());

        assertThat(results).isEmpty();
    }

    @Test
    void findByStatus_WhenNoMatchingStatus_ShouldReturnEmptyList() {
        GameUpload game = new GameUpload();
        game.setStatus(UploadStatus.APPROVED);
        entityManager.persist(game);
        entityManager.flush();

        List<GameUpload> results = gameUploadRepository.findByStatus(UploadStatus.PENDING);

        assertThat(results).isEmpty();
    }
}