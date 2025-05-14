package com.xtu.stream_game.controller;

import com.xtu.stream_game.entity.Player;
import com.xtu.stream_game.service.PlayerService;
import com.xtu.stream_game.service.EmailService;
import com.xtu.stream_game.service.impl.EmailServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/players")
public class PlayerController {

    @Autowired
    private PlayerService playerService;
    
    @Autowired
    private EmailService emailService;

    // 获取所有玩家
    @GetMapping
    public ResponseEntity<List<Player>> getAllPlayers() {
        List<Player> players = playerService.getAllPlayers();
        return new ResponseEntity<>(players, HttpStatus.OK);
    }

    // 根据ID获取玩家
    @GetMapping("/id/{playerId}")
    public ResponseEntity<Player> getPlayerById(@PathVariable Integer playerId) {
        Player player = playerService.getPlayerById(playerId);
        if (player == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(player, HttpStatus.OK);
    }

    // 根据用户名获取玩家
    @GetMapping("/username/{username}")
    public ResponseEntity<Player> getPlayerByUsername(@PathVariable String username) {
        Player player = playerService.getPlayerByUsername(username);
        if (player == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(player, HttpStatus.OK);
    }

    // 根据邮箱获取玩家
    @GetMapping("/email/{email}")
    public ResponseEntity<Player> getPlayerByEmail(@PathVariable String email) {
        Player player = playerService.getPlayerByEmail(email);
        if (player == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(player, HttpStatus.OK);
    }

    // 发送验证码
    @PostMapping("/send-verification")
    public ResponseEntity<Void> sendVerificationCode(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        if (email == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        
        // 检查邮箱是否已被注册
        if (playerService.getPlayerByEmail(email) != null) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
        
        String verificationCode = ((EmailServiceImpl) emailService).generateVerificationCode();
        emailService.sendVerificationEmail(email, verificationCode);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    // 验证邮箱
    @PostMapping("/verify-email")
    public ResponseEntity<Void> verifyEmail(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String verificationCode = request.get("verificationCode");
        
        if (email == null || verificationCode == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        
        if (emailService.verifyEmail(email, verificationCode)) {
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }

    // 玩家注册
    @PostMapping("/register")
    public ResponseEntity<Player> register(@RequestBody Map<String, Object> request) {
        String email = (String) request.get("email");
        String verificationCode = (String) request.get("verificationCode");
        String username = (String) request.get("username");
        String password = (String) request.get("password");
        
        // 记录注册请求
        org.slf4j.LoggerFactory.getLogger(PlayerController.class)
            .info("收到注册请求: username={}, email={}, verificationCode={}", username, email, verificationCode);
        
        if (email == null || verificationCode == null || username == null || password == null) {
            org.slf4j.LoggerFactory.getLogger(PlayerController.class)
                .warn("注册参数不完整: email={}, username={}", email, username);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        
        // 检查用户名是否已存在
        if (playerService.getPlayerByUsername(username) != null) {
            org.slf4j.LoggerFactory.getLogger(PlayerController.class)
                .warn("用户名已存在: {}", username);
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
        
        // 检查邮箱是否已存在
        if (playerService.getPlayerByEmail(email) != null) {
            org.slf4j.LoggerFactory.getLogger(PlayerController.class)
                .warn("邮箱已存在: {}", email);
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
        
        // 检查验证码是否有效（不验证，因为前端已经验证过）
        boolean isCodeValid = emailService.checkEmailAndVerificationCode(email, verificationCode);
        org.slf4j.LoggerFactory.getLogger(PlayerController.class)
            .info("验证码检查结果: {}", isCodeValid ? "有效" : "无效");
            
        if (!isCodeValid) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        Player player = new Player();
        player.setUsername(username);
        player.setPassword(password);
        player.setEmail(email);
        
        try {
            Player registeredPlayer = playerService.register(player);
            if (registeredPlayer == null) {
                org.slf4j.LoggerFactory.getLogger(PlayerController.class)
                    .error("注册失败: username={}, email={}", username, email);
                return new ResponseEntity<>(HttpStatus.CONFLICT);
            }
            
            org.slf4j.LoggerFactory.getLogger(PlayerController.class)
                .info("注册成功: playerId={}, username={}", registeredPlayer.getPlayerId(), username);
            return new ResponseEntity<>(registeredPlayer, HttpStatus.CREATED);
        } catch (Exception e) {
            org.slf4j.LoggerFactory.getLogger(PlayerController.class)
                .error("注册过程发生异常: {}", e.getMessage(), e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // 玩家登录
    @PostMapping("/login")
    public ResponseEntity<Player> login(@RequestBody Map<String, String> credentials) {
        String username = credentials.get("username");
        String password = credentials.get("password");
        
        if (username == null || password == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        
        Player player = playerService.login(username, password);
        if (player == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        
        return new ResponseEntity<>(player, HttpStatus.OK);
    }

    // 更新玩家信息
    @PutMapping("/id/{playerId}")
    public ResponseEntity<Player> updatePlayer(@PathVariable Integer playerId, @RequestBody Player playerDetails) {
        Player updatedPlayer = playerService.updatePlayer(playerId, playerDetails);
        if (updatedPlayer == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(updatedPlayer, HttpStatus.OK);
    }

    // 删除玩家
    @DeleteMapping("/id/{playerId}")
    public ResponseEntity<Void> deletePlayer(@PathVariable Integer playerId) {
        Player player = playerService.getPlayerById(playerId);
        if (player == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        playerService.deletePlayer(playerId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
} 