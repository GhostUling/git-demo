package com.xtu.stream_game.repository;

import com.xtu.stream_game.entity.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Date;
import java.util.List;

@Repository
public interface PlayerRepository extends JpaRepository<Player, Integer> {
    // 根据用户名查找玩家
    Player findByUsername(String username);
    
    // 根据邮箱查找玩家
    Player findByEmail(String email);
    
    // 根据用户名和密码查找玩家（登录验证）
    Player findByUsernameAndPassword(String username, String password);
    
    // 查找在某一时间段内注册的玩家
    List<Player> findByRegistrationTimeBetween(Date startDate, Date endDate);
} 