package com.xtu.stream_game.service;

import com.xtu.stream_game.entity.PlayerGameLibrary;
import java.util.List;

public interface PlayerGameLibraryService {
    // 添加游戏到库
    PlayerGameLibrary addGameToLibrary(Long playerId, Long gameId);
    
    // 获取玩家的所有游戏
    List<PlayerGameLibrary> getPlayerGames(Long playerId);
    
    // 获取玩家的特定游戏
    PlayerGameLibrary getPlayerGame(Long playerId, Long gameId);
    
    // 获取玩家已安装的游戏
    List<PlayerGameLibrary> getInstalledGames(Long playerId);
    
    // 按游戏类型获取游戏
    List<PlayerGameLibrary> getGamesByType(Long playerId, String gameType);
    
    // 按最后游玩时间排序
    List<PlayerGameLibrary> getGamesOrderByLastPlayed(Long playerId);
    
    // 按总游戏时长排序
    List<PlayerGameLibrary> getGamesOrderByPlayTime(Long playerId);
    
    // 安装游戏
    PlayerGameLibrary installGame(Long playerId, Long gameId, String installPath);
    
    // 卸载游戏
    PlayerGameLibrary uninstallGame(Long playerId, Long gameId);
    
    // 更新游戏设置
    PlayerGameLibrary updateGameSettings(Long playerId, Long gameId, String settings);
    
    // 更新游戏存档路径
    PlayerGameLibrary updateSavePath(Long playerId, Long gameId, String savePath);
    
    // 更新游戏时长
    PlayerGameLibrary updatePlayTime(Long playerId, Long gameId, Long playTime);
    
    // 从库中移除游戏
    void removeGameFromLibrary(Long playerId, Long gameId);
} 