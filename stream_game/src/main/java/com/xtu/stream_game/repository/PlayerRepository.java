package com.xtu.stream_game.repository;

import com.xtu.stream_game.entity.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PlayerRepository extends JpaRepository<Player, Integer> {
    // 根据用户名查找玩家
    @Query("SELECT p FROM Player p WHERE p.username = :username")
    Player findByUsername(@Param("username") String username);
    
    // 根据邮箱查找玩家
    @Query("SELECT p FROM Player p WHERE p.email = :email")
    Player findByEmail(@Param("email") String email);
    
    // 根据用户名和密码查找玩家（登录验证）
    @Query("SELECT p FROM Player p WHERE p.username = :username AND p.password = :password")
    Player findByUsernameAndPassword(@Param("username") String username, @Param("password") String password);
} 