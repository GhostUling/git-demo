package com.xtu.stream_game.service;

import com.xtu.stream_game.entity.Player;
import com.xtu.stream_game.repository.PlayerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PlayerServiceTest {

    @Mock
    private PlayerRepository playerRepository;

    @InjectMocks
    private PlayerServiceImpl playerService;

    @Test
    void register_ShouldEncryptPassword() {
        Player player = new Player();
        player.setPassword("plaintext");
        when(playerRepository.save(any())).thenReturn(player);

        Player result = playerService.register(player);
        assertNotEquals("plaintext", result.getPassword());
    }
}