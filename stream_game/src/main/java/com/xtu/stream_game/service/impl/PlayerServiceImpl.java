package com.xtu.stream_game.service.impl;

import com.xtu.stream_game.entity.Player;
import com.xtu.stream_game.repository.PlayerRepository;
import com.xtu.stream_game.service.PlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class PlayerServiceImpl implements PlayerService {

    @Autowired
    private PlayerRepository playerRepository;

    @Override
    public List<Player> getAllPlayers() {
        return playerRepository.findAll();
    }

    @Override
    public Player getPlayerById(int playerId) {
        return playerRepository.findById(playerId).orElse(null);
    }

    @Override
    public Player getPlayerByUsername(String username) {
        return playerRepository.findByUsername(username);
    }

    @Override
    public Player getPlayerByEmail(String email) {
        return playerRepository.findByEmail(email);
    }

    @Override
    public Player login(String username, String password) {
        return playerRepository.findByUsernameAndPassword(username, password);
    }

    @Override
    public Player register(Player player) {
        // 检查用户名是否已存在
        Player existingPlayer = playerRepository.findByUsername(player.getUsername());
        if (existingPlayer != null) {
            return null; // 用户名已存在
        }
        
        // 检查邮箱是否已存在
        existingPlayer = playerRepository.findByEmail(player.getEmail());
        if (existingPlayer != null) {
            return null; // 邮箱已存在
        }
        
        // 设置注册时间
        player.setRegistrationTime(new Date());
        
        // 设置默认余额（如果为null）
        if (player.getBalance() == null) {
            player.setBalance(new BigDecimal("0.00"));
        }
        
        return playerRepository.save(player);
    }

    @Override
    public Player updatePlayer(int playerId, Player playerDetails) {
        Optional<Player> playerOpt = playerRepository.findById(playerId);
        
        if (playerOpt.isPresent()) {
            Player player = playerOpt.get();
            
            // 更新玩家信息
            if (playerDetails.getUsername() != null) {
                player.setUsername(playerDetails.getUsername());
            }
            
            if (playerDetails.getPassword() != null) {
                player.setPassword(playerDetails.getPassword());
            }
            
            if (playerDetails.getEmail() != null) {
                player.setEmail(playerDetails.getEmail());
            }
            
            return playerRepository.save(player);
        }
        
        return null;
    }

    @Override
    public void deletePlayer(int playerId) {
        playerRepository.deleteById(playerId);
    }
} 