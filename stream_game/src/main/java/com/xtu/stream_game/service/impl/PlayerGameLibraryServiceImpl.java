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
    public PlayerGameLibrary addGameToLibrary(Integer playerId, Integer gameId) {
        Player player = playerRepository.findById(playerId)
                .orElseThrow(() -> new RuntimeException("玩家不存在"));
        
        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new RuntimeException("游戏不存在"));

        // 检查游戏是否已在库中
        if (libraryRepository.findByPlayerPlayerIdAndGameGameId(playerId, gameId).isPresent()) {
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
    public PlayerGameLibrary getGameFromLibrary(Integer playerId, Integer gameId) {
        return libraryRepository.findByPlayerPlayerIdAndGameGameId(playerId, gameId)
                .orElseThrow(() -> new RuntimeException("游戏不在库中"));
    }

    @Override
    public List<PlayerGameLibrary> getPlayerLibrary(Integer playerId) {
        return libraryRepository.findByPlayerPlayerId(playerId);
    }

    @Override
    public List<PlayerGameLibrary> getFavoriteGames(Integer playerId) {
        return libraryRepository.findByPlayerPlayerIdAndIsFavorite(playerId, true);
    }

    @Override
    public List<PlayerGameLibrary> getRecentlyPlayedGames(Integer playerId) {
        return libraryRepository.findByPlayerPlayerIdOrderByLastPlayTimeDesc(playerId);
    }

    @Override
    public List<PlayerGameLibrary> getMostPlayedGames(Integer playerId) {
        return libraryRepository.findByPlayerIdOrderByPlayTimeDesc(playerId);
    }

    @Override
    @Transactional
    public PlayerGameLibrary updateGamePlayTime(Integer playerId, Integer gameId, Integer playTime) {
        PlayerGameLibrary library = getGameFromLibrary(playerId, gameId);
        library.setPlayTime(playTime);
        library.setLastPlayTime(LocalDateTime.now());
        return libraryRepository.save(library);
    }

    @Override
    @Transactional
    public PlayerGameLibrary toggleFavorite(Integer playerId, Integer gameId) {
        PlayerGameLibrary library = getGameFromLibrary(playerId, gameId);
        library.setIsFavorite(!library.getIsFavorite());
        return libraryRepository.save(library);
    }

    @Override
    @Transactional
    public PlayerGameLibrary updateGameNotes(Integer playerId, Integer gameId, String notes) {
        PlayerGameLibrary library = getGameFromLibrary(playerId, gameId);
        library.setNotes(notes);
        return libraryRepository.save(library);
    }

    @Override
    @Transactional
    public void removeGameFromLibrary(Integer playerId, Integer gameId) {
        PlayerGameLibrary library = getGameFromLibrary(playerId, gameId);
        libraryRepository.delete(library);
    }
} 