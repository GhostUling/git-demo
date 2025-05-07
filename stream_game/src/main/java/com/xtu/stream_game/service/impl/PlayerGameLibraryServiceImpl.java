package com.xtu.stream_game.service.impl;

import com.xtu.stream_game.entity.*;
import com.xtu.stream_game.repository.PlayerGameLibraryRepository;
import com.xtu.stream_game.repository.PlayerRepository;
import com.xtu.stream_game.repository.GameRepository;
import com.xtu.stream_game.service.PlayerGameLibraryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PlayerGameLibraryServiceImpl implements PlayerGameLibraryService {

    @Autowired
    private PlayerGameLibraryRepository libraryRepository;

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private GameRepository gameRepository;

    @Override
    @Transactional
    public PlayerGameLibrary addGameToLibrary(Long playerId, Long gameId) {
        Player player = playerRepository.findById(playerId)
                .orElseThrow(() -> new RuntimeException("玩家不存在"));
        
        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new RuntimeException("游戏不存在"));

        // 检查游戏是否已在库中
        if (libraryRepository.findByPlayerIdAndGameId(playerId, gameId).isPresent()) {
            throw new RuntimeException("游戏已在库中");
        }

        PlayerGameLibrary library = new PlayerGameLibrary();
        library.setPlayer(player);
        library.setGame(game);
        library.setPurchaseTime(LocalDateTime.now());
        library.setLastPlayTime(LocalDateTime.now());
        library.setPlayTime(0);
        library.setIsFavorite(false);

        return libraryRepository.save(library);
    }

    @Override
    public PlayerGameLibrary getGameFromLibrary(Long playerId, Long gameId) {
        return libraryRepository.findByPlayerIdAndGameId(playerId, gameId)
                .orElseThrow(() -> new RuntimeException("游戏不在库中"));
    }

    @Override
    public List<PlayerGameLibrary> getPlayerLibrary(Long playerId) {
        return libraryRepository.findByPlayerId(playerId);
    }

    @Override
    public List<PlayerGameLibrary> getFavoriteGames(Long playerId) {
        return libraryRepository.findByPlayerIdAndIsFavorite(playerId, true);
    }

    @Override
    public List<PlayerGameLibrary> getRecentlyPlayedGames(Long playerId) {
        return libraryRepository.findByPlayerIdOrderByLastPlayTimeDesc(playerId);
    }

    @Override
    public List<PlayerGameLibrary> getMostPlayedGames(Long playerId) {
        return libraryRepository.findByPlayerIdOrderByPlayTimeDesc(playerId);
    }

    @Override
    @Transactional
    public PlayerGameLibrary updateGamePlayTime(Long playerId, Long gameId, Integer playTime) {
        PlayerGameLibrary library = getGameFromLibrary(playerId, gameId);
        library.setPlayTime(playTime);
        library.setLastPlayTime(LocalDateTime.now());
        return libraryRepository.save(library);
    }

    @Override
    @Transactional
    public PlayerGameLibrary toggleFavorite(Long playerId, Long gameId) {
        PlayerGameLibrary library = getGameFromLibrary(playerId, gameId);
        library.setIsFavorite(!library.getIsFavorite());
        return libraryRepository.save(library);
    }

    @Override
    @Transactional
    public PlayerGameLibrary updateGameNotes(Long playerId, Long gameId, String notes) {
        PlayerGameLibrary library = getGameFromLibrary(playerId, gameId);
        library.setNotes(notes);
        return libraryRepository.save(library);
    }

    @Override
    @Transactional
    public void removeGameFromLibrary(Long playerId, Long gameId) {
        PlayerGameLibrary library = getGameFromLibrary(playerId, gameId);
        libraryRepository.delete(library);
    }
} 