package com.xtu.stream_game.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.math.BigDecimal;

@Entity
public class Game {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long gameId;
    private String gameName;
    private BigDecimal price;
    private String description;
    private String installPackagePath;
    private String gameType;

    // 新增属性，关联 Developer 类
    @ManyToOne
    @JoinColumn(name = "developer_id")
    private Developer developer;

    // Getters and Setters
    public Long getGameId() {
        return gameId;
    }

    public void setGameId(Long gameId) {
        this.gameId = gameId;
    }

    public String getGameName() {
        return gameName;
    }

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getInstallPackagePath() {
        return installPackagePath;
    }

    public void setInstallPackagePath(String installPackagePath) {
        this.installPackagePath = installPackagePath;
    }

    public Developer getDeveloper() {
        return developer;
    }

    public void setDeveloper(Developer developer) {
        this.developer = developer;
    }

    public String getGameType() {
        return gameType;
    }

    public void setGameType(String gameType) {
        this.gameType = gameType;
    }
}    