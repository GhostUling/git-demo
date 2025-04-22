package com.xtu.stream_game.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "player_game_library")
public class PlayerGameLibrary {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "player_id", nullable = false)
    private Player player;

    @ManyToOne
    @JoinColumn(name = "game_id", nullable = false)
    private Game game;

    private LocalDateTime purchaseTime;
    private LocalDateTime lastPlayTime;
    private Long totalPlayTime; // 总游戏时长（分钟）
    private boolean isInstalled;
    private String installPath;
    private String savePath;
    private String gameSettings; // 游戏设置（JSON格式存储）

    public PlayerGameLibrary() {
    }

    public PlayerGameLibrary(Player player, Game game) {
        this.player = player;
        this.game = game;
        this.purchaseTime = LocalDateTime.now();
        this.totalPlayTime = 0L;
        this.isInstalled = false;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public LocalDateTime getPurchaseTime() {
        return purchaseTime;
    }

    public void setPurchaseTime(LocalDateTime purchaseTime) {
        this.purchaseTime = purchaseTime;
    }

    public LocalDateTime getLastPlayTime() {
        return lastPlayTime;
    }

    public void setLastPlayTime(LocalDateTime lastPlayTime) {
        this.lastPlayTime = lastPlayTime;
    }

    public Long getTotalPlayTime() {
        return totalPlayTime;
    }

    public void setTotalPlayTime(Long totalPlayTime) {
        this.totalPlayTime = totalPlayTime;
    }

    public boolean isInstalled() {
        return isInstalled;
    }

    public void setInstalled(boolean installed) {
        isInstalled = installed;
    }

    public String getInstallPath() {
        return installPath;
    }

    public void setInstallPath(String installPath) {
        this.installPath = installPath;
    }

    public String getSavePath() {
        return savePath;
    }

    public void setSavePath(String savePath) {
        this.savePath = savePath;
    }

    public String getGameSettings() {
        return gameSettings;
    }

    public void setGameSettings(String gameSettings) {
        this.gameSettings = gameSettings;
    }
} 