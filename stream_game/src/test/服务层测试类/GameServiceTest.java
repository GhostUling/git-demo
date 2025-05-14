package com.xtu.stream_game.service;

import com.xtu.stream_game.entity.Game;
import com.xtu.stream_game.repository.GameRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Collections;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GameServiceTest {

    @Mock
    private GameRepository gameRepository;

    @InjectMocks
    private GameServiceImpl gameService;

    @Test
    void addGame_ShouldReturnSavedGame() {
        Game game = new Game();
        when(gameRepository.save(any())).thenReturn(game);

        Game result = gameService.addGame(game);
        assertNotNull(result);
        verify(gameRepository).save(game);
    }
}