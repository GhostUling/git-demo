package com.xtu.stream_game.service.impl;

import com.xtu.stream_game.entity.PlayerGame;
import com.xtu.stream_game.entity.Player;
import com.xtu.stream_game.entity.Game;
import com.xtu.stream_game.repository.PlayerGameRepository;
import com.xtu.stream_game.repository.PlayerRepository;
import com.xtu.stream_game.repository.GameRepository;
import com.xtu.stream_game.service.PlayerGameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class PlayerGameServiceImpl implements PlayerGameService {

    @Autowired
    private PlayerGameRepository playerGameRepository;
    
    @Autowired
    private PlayerRepository playerRepository;
    
    @Autowired
    private GameRepository gameRepository;

    @Override
    public List<PlayerGame> getAllPlayerGames() {
        return playerGameRepository.findAll();
    }

    @Override
    public List<PlayerGame> getGamesByPlayer(Long playerId) {
        Optional<Player> playerOpt = playerRepository.findById(playerId);
        if (playerOpt.isPresent()) {
            return playerGameRepository.findByPlayer(playerOpt.get());
        }
        return List.of();
    }

    @Override
    public PlayerGame getPlayerGame(Long playerId, Long gameId) {
        Optional<Player> playerOpt = playerRepository.findById(playerId);
        Optional<Game> gameOpt = gameRepository.findById(gameId);
        
        if (playerOpt.isPresent() && gameOpt.isPresent()) {
            return playerGameRepository.findByPlayerAndGame(playerOpt.get(), gameOpt.get());
        }
        return null;
    }

    @Override
    public List<PlayerGame> getPlayerGamesByGameType(Long playerId, String gameType) {
        Optional<Player> playerOpt = playerRepository.findById(playerId);
        if (playerOpt.isPresent()) {
            return playerGameRepository.findByPlayerAndGame_GameType(playerOpt.get(), gameType);
        }
        return List.of();
    }

    @Override
    public List<PlayerGame> getPlayerGamesSortedByPlayTime(Long playerId) {
        Optional<Player> playerOpt = playerRepository.findById(playerId);
        if (playerOpt.isPresent()) {
            return playerGameRepository.findByPlayerOrderByPlayTimeMinutesDesc(playerOpt.get());
        }
        return List.of();
    }

    @Override
    public PlayerGame addPlayerGame(PlayerGame playerGame) {
        // 设置购买日期为当前日期
        if (playerGame.getPurchaseDate() == null) {
            playerGame.setPurchaseDate(new Date());
        }
        
        // 初始化游戏时间为0
        if (playerGame.getPlayTimeMinutes() <= 0) {
            playerGame.setPlayTimeMinutes(0);
        }
        
        return playerGameRepository.save(playerGame);
    }

    @Override
    public PlayerGame updatePlayerGame(Long id, PlayerGame playerGameDetails) {
        Optional<PlayerGame> playerGameOpt = playerGameRepository.findById(id);
        
        if (playerGameOpt.isPresent()) {
            PlayerGame playerGame = playerGameOpt.get();
            
            // 更新游戏时间
            if (playerGameDetails.getPlayTimeMinutes() > 0) {
                playerGame.setPlayTimeMinutes(playerGameDetails.getPlayTimeMinutes());
            }
            
            // 更新最后游玩时间
            if (playerGameDetails.getLastPlayedDate() != null) {
                playerGame.setLastPlayedDate(playerGameDetails.getLastPlayedDate());
            }
            
            return playerGameRepository.save(playerGame);
        }
        
        return null;
    }

    @Override
    public void removePlayerGame(Long id) {
        playerGameRepository.deleteById(id);
    }
} 