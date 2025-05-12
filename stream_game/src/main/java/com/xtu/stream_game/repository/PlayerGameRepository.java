package com.xtu.stream_game.repository;

import com.xtu.stream_game.entity.Game;
import com.xtu.stream_game.entity.Player;
import com.xtu.stream_game.entity.PlayerGame;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PlayerGameRepository extends JpaRepository<PlayerGame, Integer> {
    // 查找玩家的所有游戏
    List<PlayerGame> findByPlayer(Player player);
    
    // 查找玩家的特定游戏
    PlayerGame findByPlayerAndGame(Player player, Game game);
    
    // 根据游戏类型查找玩家游戏
    List<PlayerGame> findByPlayerAndGame_GameType(Player player, String gameType);
    
    // 按照游玩时长排序获取玩家的游戏
    List<PlayerGame> findByPlayerOrderByPlayTimeMinutesDesc(Player player);
} 