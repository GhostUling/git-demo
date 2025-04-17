package com.xtu.stream_game.service;

import com.xtu.stream_game.entity.PlayerGame;
import java.util.List;

public interface PlayerGameService {
    // 获取所有玩家游戏记录
    List<PlayerGame> getAllPlayerGames();
    
    // 获取特定玩家的所有游戏
    List<PlayerGame> getGamesByPlayer(int playerId);
    
    // 获取特定玩家的特定游戏记录
    PlayerGame getPlayerGame(int playerId, int gameId);
    
    // 根据游戏类型查找玩家游戏
    List<PlayerGame> getPlayerGamesByGameType(int playerId, String gameType);
    
    // 按照游玩时长排序获取玩家的游戏
    List<PlayerGame> getPlayerGamesSortedByPlayTime(int playerId);
    
    // 添加玩家游戏记录（玩家购买游戏）
    PlayerGame addPlayerGame(PlayerGame playerGame);
    
    // 更新玩家游戏信息（如游戏时间等）
    PlayerGame updatePlayerGame(int id, PlayerGame playerGameDetails);
    
    // 移除玩家游戏记录
    void removePlayerGame(int id);
} 