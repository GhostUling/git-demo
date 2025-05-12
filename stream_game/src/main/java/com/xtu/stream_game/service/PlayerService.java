package com.xtu.stream_game.service;

import com.xtu.stream_game.entity.Player;
import java.util.List;

public interface PlayerService {
    // 获取所有玩家
    List<Player> getAllPlayers();
    
    // 根据ID获取玩家
    Player getPlayerById(Integer playerId);
    
    // 根据用户名获取玩家
    Player getPlayerByUsername(String username);
    
    // 根据邮箱获取玩家
    Player getPlayerByEmail(String email);
    
    // 玩家登录
    Player login(String username, String password);
    
    // 玩家注册
    Player register(Player player);
    
    // 更新玩家信息
    Player updatePlayer(Integer playerId, Player playerDetails);
    
    // 删除玩家
    void deletePlayer(Integer playerId);
} 