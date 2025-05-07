package com.xtu.stream_game.service;

import com.xtu.stream_game.entity.PlayerGameLibrary;
import java.util.List;

public interface PlayerGameLibraryService {
    PlayerGameLibrary addGameToLibrary(Long playerId, Long gameId);
    PlayerGameLibrary getGameFromLibrary(Long playerId, Long gameId);
    List<PlayerGameLibrary> getPlayerLibrary(Long playerId);
    List<PlayerGameLibrary> getFavoriteGames(Long playerId);
    List<PlayerGameLibrary> getRecentlyPlayedGames(Long playerId);
    List<PlayerGameLibrary> getMostPlayedGames(Long playerId);
    PlayerGameLibrary updateGamePlayTime(Long playerId, Long gameId, Integer playTime);
    PlayerGameLibrary toggleFavorite(Long playerId, Long gameId);
    PlayerGameLibrary updateGameNotes(Long playerId, Long gameId, String notes);
    void removeGameFromLibrary(Long playerId, Long gameId);
} 