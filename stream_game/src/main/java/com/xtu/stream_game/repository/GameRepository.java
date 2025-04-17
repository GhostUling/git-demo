package com.xtu.stream_game.repository;

import com.xtu.stream_game.entity.Game;
import com.xtu.stream_game.entity.Developer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface GameRepository extends JpaRepository<Game, Integer> {
    // 根据游戏名称查找游戏
    Game findByGameName(String gameName);
    
    // 根据开发者查找游戏
    List<Game> findByDeveloper(Developer developer);
    
    // 根据开发者ID查找游戏
    List<Game> findByDeveloperDeveloperId(int developerId);
    
    // 根据游戏类型查找游戏
    List<Game> findByGameType(String gameType);
} 