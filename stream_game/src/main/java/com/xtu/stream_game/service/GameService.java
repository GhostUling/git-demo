package com.xtu.stream_game.service;

import com.xtu.stream_game.entity.Game;
import com.xtu.stream_game.entity.Developer;
import java.util.List;

public interface GameService {
    // 获取所有游戏
    List<Game> getAllGames();
    
    // 根据ID获取游戏
    Game getGameById(int gameId);
    
    // 根据名称获取游戏
    Game getGameByName(String gameName);
    
    // 根据开发者获取游戏
    List<Game> getGamesByDeveloper(Developer developer);
    
    // 根据开发者ID获取游戏
    List<Game> getGamesByDeveloperId(int developerId);
    
    // 根据游戏类型获取游戏
    List<Game> getGamesByType(String gameType);
    
    // 添加游戏
    Game addGame(Game game);
    
    // 更新游戏信息
    Game updateGame(int gameId, Game gameDetails);
    
    // 删除游戏
    void deleteGame(int gameId);
} 