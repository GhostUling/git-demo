package com.xtu.stream_game.service;

import com.xtu.stream_game.entity.PlayerGameLibrary;
import java.util.List;

public interface PlayerGameLibraryService {
    PlayerGameLibrary addGameToLibrary(Integer playerId, Integer gameId);
    PlayerGameLibrary getGameFromLibrary(Integer playerId, Integer gameId);
    List<PlayerGameLibrary> getPlayerLibrary(Integer playerId);
    List<PlayerGameLibrary> getFavoriteGames(Integer playerId);
    List<PlayerGameLibrary> getRecentlyPlayedGames(Integer playerId);
    List<PlayerGameLibrary> getMostPlayedGames(Integer playerId);
    PlayerGameLibrary updateGamePlayTime(Integer playerId, Integer gameId, Integer playTime);
    PlayerGameLibrary toggleFavorite(Integer playerId, Integer gameId);
    PlayerGameLibrary updateGameNotes(Integer playerId, Integer gameId, String notes);
    void removeGameFromLibrary(Integer playerId, Integer gameId);
} 