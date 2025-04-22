package com.xtu.stream_game.service.impl;

import com.xtu.stream_game.entity.Game;
import com.xtu.stream_game.entity.Player;
import com.xtu.stream_game.entity.PlayerGameLibrary;
import com.xtu.stream_game.repository.PlayerGameLibraryRepository;
import com.xtu.stream_game.service.GameService;
import com.xtu.stream_game.service.PlayerGameLibraryService;
import com.xtu.stream_game.service.PlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PlayerGameLibraryServiceImpl implements PlayerGameLibraryService {

    @Autowired
    private PlayerGameLibraryRepository playerGameLibraryRepository;
    
    @Autowired
    private PlayerService playerService;
    
    @Autowired
    private GameService gameService;

    @Override
    @Transactional
    public PlayerGameLibrary addGameToLibrary(Long playerId, Long gameId) {
        Player player = playerService.getPlayerById(playerId);
        Game game = gameService.getGameById(gameId);
        
        if (player == null || game == null) {
            return null;
        }
        
        // 检查是否已经拥有该游戏
        PlayerGameLibrary existingLibrary = playerGameLibraryRepository
            .findByPlayerIdAndGameId(playerId, gameId)
            .orElse(null);
            
        if (existingLibrary != null) {
            return existingLibrary;
        }
        
        PlayerGameLibrary library = new PlayerGameLibrary(player, game);
        return playerGameLibraryRepository.save(library);
    }

    @Override
    public List<PlayerGameLibrary> getPlayerGames(Long playerId) {
        return playerGameLibraryRepository.findByPlayerId(playerId);
    }

    @Override
    public PlayerGameLibrary getPlayerGame(Long playerId, Long gameId) {
        return playerGameLibraryRepository.findByPlayerIdAndGameId(playerId, gameId)
            .orElse(null);
    }

    @Override
    public List<PlayerGameLibrary> getInstalledGames(Long playerId) {
        return playerGameLibraryRepository.findInstalledGamesByPlayerId(playerId);
    }

    @Override
    public List<PlayerGameLibrary> getGamesByType(Long playerId, String gameType) {
        return playerGameLibraryRepository.findByPlayerIdAndGameType(playerId, gameType);
    }

    @Override
    public List<PlayerGameLibrary> getGamesOrderByLastPlayed(Long playerId) {
        return playerGameLibraryRepository.findByPlayerIdOrderByLastPlayTimeDesc(playerId);
    }

    @Override
    public List<PlayerGameLibrary> getGamesOrderByPlayTime(Long playerId) {
        return playerGameLibraryRepository.findByPlayerIdOrderByTotalPlayTimeDesc(playerId);
    }

    @Override
    @Transactional
    public PlayerGameLibrary installGame(Long playerId, Long gameId, String installPath) {
        PlayerGameLibrary library = getPlayerGame(playerId, gameId);
        if (library == null) {
            return null;
        }
        
        library.setInstalled(true);
        library.setInstallPath(installPath);
        return playerGameLibraryRepository.save(library);
    }

    @Override
    @Transactional
    public PlayerGameLibrary uninstallGame(Long playerId, Long gameId) {
        PlayerGameLibrary library = getPlayerGame(playerId, gameId);
        if (library == null) {
            return null;
        }
        
        library.setInstalled(false);
        library.setInstallPath(null);
        return playerGameLibraryRepository.save(library);
    }

    @Override
    @Transactional
    public PlayerGameLibrary updateGameSettings(Long playerId, Long gameId, String settings) {
        PlayerGameLibrary library = getPlayerGame(playerId, gameId);
        if (library == null) {
            return null;
        }
        
        library.setGameSettings(settings);
        return playerGameLibraryRepository.save(library);
    }

    @Override
    @Transactional
    public PlayerGameLibrary updateSavePath(Long playerId, Long gameId, String savePath) {
        PlayerGameLibrary library = getPlayerGame(playerId, gameId);
        if (library == null) {
            return null;
        }
        
        library.setSavePath(savePath);
        return playerGameLibraryRepository.save(library);
    }

    @Override
    @Transactional
    public PlayerGameLibrary updatePlayTime(Long playerId, Long gameId, Long playTime) {
        PlayerGameLibrary library = getPlayerGame(playerId, gameId);
        if (library == null) {
            return null;
        }
        
        library.setTotalPlayTime(library.getTotalPlayTime() + playTime);
        library.setLastPlayTime(LocalDateTime.now());
        return playerGameLibraryRepository.save(library);
    }

    @Override
    @Transactional
    public void removeGameFromLibrary(Long playerId, Long gameId) {
        PlayerGameLibrary library = getPlayerGame(playerId, gameId);
        if (library != null) {
            playerGameLibraryRepository.delete(library);
        }
    }
} 