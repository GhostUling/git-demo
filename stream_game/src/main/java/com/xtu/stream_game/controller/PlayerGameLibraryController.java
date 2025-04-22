package com.xtu.stream_game.controller;

import com.xtu.stream_game.entity.PlayerGameLibrary;
import com.xtu.stream_game.service.PlayerGameLibraryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/player-games")
public class PlayerGameLibraryController {

    @Autowired
    private PlayerGameLibraryService playerGameLibraryService;

    // 添加游戏到库
    @PostMapping("/{playerId}/games/{gameId}")
    public ResponseEntity<PlayerGameLibrary> addGameToLibrary(
            @PathVariable Long playerId,
            @PathVariable Long gameId) {
        PlayerGameLibrary library = playerGameLibraryService.addGameToLibrary(playerId, gameId);
        if (library == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(library, HttpStatus.CREATED);
    }

    // 获取玩家的所有游戏
    @GetMapping("/{playerId}")
    public ResponseEntity<List<PlayerGameLibrary>> getPlayerGames(@PathVariable Long playerId) {
        List<PlayerGameLibrary> games = playerGameLibraryService.getPlayerGames(playerId);
        return new ResponseEntity<>(games, HttpStatus.OK);
    }

    // 获取玩家的特定游戏
    @GetMapping("/{playerId}/games/{gameId}")
    public ResponseEntity<PlayerGameLibrary> getPlayerGame(
            @PathVariable Long playerId,
            @PathVariable Long gameId) {
        PlayerGameLibrary library = playerGameLibraryService.getPlayerGame(playerId, gameId);
        if (library == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(library, HttpStatus.OK);
    }

    // 获取玩家已安装的游戏
    @GetMapping("/{playerId}/installed")
    public ResponseEntity<List<PlayerGameLibrary>> getInstalledGames(@PathVariable Long playerId) {
        List<PlayerGameLibrary> games = playerGameLibraryService.getInstalledGames(playerId);
        return new ResponseEntity<>(games, HttpStatus.OK);
    }

    // 按游戏类型获取游戏
    @GetMapping("/{playerId}/type/{gameType}")
    public ResponseEntity<List<PlayerGameLibrary>> getGamesByType(
            @PathVariable Long playerId,
            @PathVariable String gameType) {
        List<PlayerGameLibrary> games = playerGameLibraryService.getGamesByType(playerId, gameType);
        return new ResponseEntity<>(games, HttpStatus.OK);
    }

    // 按最后游玩时间排序
    @GetMapping("/{playerId}/sort/last-played")
    public ResponseEntity<List<PlayerGameLibrary>> getGamesOrderByLastPlayed(@PathVariable Long playerId) {
        List<PlayerGameLibrary> games = playerGameLibraryService.getGamesOrderByLastPlayed(playerId);
        return new ResponseEntity<>(games, HttpStatus.OK);
    }

    // 按总游戏时长排序
    @GetMapping("/{playerId}/sort/play-time")
    public ResponseEntity<List<PlayerGameLibrary>> getGamesOrderByPlayTime(@PathVariable Long playerId) {
        List<PlayerGameLibrary> games = playerGameLibraryService.getGamesOrderByPlayTime(playerId);
        return new ResponseEntity<>(games, HttpStatus.OK);
    }

    // 安装游戏
    @PostMapping("/{playerId}/games/{gameId}/install")
    public ResponseEntity<PlayerGameLibrary> installGame(
            @PathVariable Long playerId,
            @PathVariable Long gameId,
            @RequestBody Map<String, String> request) {
        String installPath = request.get("installPath");
        PlayerGameLibrary library = playerGameLibraryService.installGame(playerId, gameId, installPath);
        if (library == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(library, HttpStatus.OK);
    }

    // 卸载游戏
    @PostMapping("/{playerId}/games/{gameId}/uninstall")
    public ResponseEntity<PlayerGameLibrary> uninstallGame(
            @PathVariable Long playerId,
            @PathVariable Long gameId) {
        PlayerGameLibrary library = playerGameLibraryService.uninstallGame(playerId, gameId);
        if (library == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(library, HttpStatus.OK);
    }

    // 更新游戏设置
    @PutMapping("/{playerId}/games/{gameId}/settings")
    public ResponseEntity<PlayerGameLibrary> updateGameSettings(
            @PathVariable Long playerId,
            @PathVariable Long gameId,
            @RequestBody Map<String, String> request) {
        String settings = request.get("settings");
        PlayerGameLibrary library = playerGameLibraryService.updateGameSettings(playerId, gameId, settings);
        if (library == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(library, HttpStatus.OK);
    }

    // 更新游戏存档路径
    @PutMapping("/{playerId}/games/{gameId}/save-path")
    public ResponseEntity<PlayerGameLibrary> updateSavePath(
            @PathVariable Long playerId,
            @PathVariable Long gameId,
            @RequestBody Map<String, String> request) {
        String savePath = request.get("savePath");
        PlayerGameLibrary library = playerGameLibraryService.updateSavePath(playerId, gameId, savePath);
        if (library == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(library, HttpStatus.OK);
    }

    // 更新游戏时长
    @PutMapping("/{playerId}/games/{gameId}/play-time")
    public ResponseEntity<PlayerGameLibrary> updatePlayTime(
            @PathVariable Long playerId,
            @PathVariable Long gameId,
            @RequestBody Map<String, Long> request) {
        Long playTime = request.get("playTime");
        PlayerGameLibrary library = playerGameLibraryService.updatePlayTime(playerId, gameId, playTime);
        if (library == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(library, HttpStatus.OK);
    }

    // 从库中移除游戏
    @DeleteMapping("/{playerId}/games/{gameId}")
    public ResponseEntity<Void> removeGameFromLibrary(
            @PathVariable Long playerId,
            @PathVariable Long gameId) {
        playerGameLibraryService.removeGameFromLibrary(playerId, gameId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
} 