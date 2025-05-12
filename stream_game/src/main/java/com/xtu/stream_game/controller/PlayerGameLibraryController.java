package com.xtu.stream_game.controller;

import com.xtu.stream_game.entity.PlayerGameLibrary;
import com.xtu.stream_game.service.PlayerGameLibraryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/library")
public class PlayerGameLibraryController {

    @Autowired
    private PlayerGameLibraryService libraryService;

    @PostMapping("/{playerId}/games/{gameId}")
    public ResponseEntity<?> addGameToLibrary(
            @PathVariable Integer playerId,
            @PathVariable Integer gameId) {
        try {
            PlayerGameLibrary library = libraryService.addGameToLibrary(playerId, gameId);
            return ResponseEntity.ok(library);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", "添加游戏失败",
                    "message", e.getMessage()
            ));
        }
    }

    @GetMapping("/{playerId}/games/{gameId}")
    public ResponseEntity<?> getGameFromLibrary(
            @PathVariable Integer playerId,
            @PathVariable Integer gameId) {
        try {
            PlayerGameLibrary library = libraryService.getGameFromLibrary(playerId, gameId);
            return ResponseEntity.ok(library);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", "获取游戏失败",
                    "message", e.getMessage()
            ));
        }
    }

    @GetMapping("/{playerId}/games")
    public ResponseEntity<?> getPlayerLibrary(@PathVariable Integer playerId) {
        try {
            List<PlayerGameLibrary> library = libraryService.getPlayerLibrary(playerId);
            return ResponseEntity.ok(library);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", "获取游戏库失败",
                    "message", e.getMessage()
            ));
        }
    }

    @GetMapping("/{playerId}/favorites")
    public ResponseEntity<?> getFavoriteGames(@PathVariable Integer playerId) {
        try {
            List<PlayerGameLibrary> favorites = libraryService.getFavoriteGames(playerId);
            return ResponseEntity.ok(favorites);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", "获取收藏游戏失败",
                    "message", e.getMessage()
            ));
        }
    }

    @GetMapping("/{playerId}/recent")
    public ResponseEntity<?> getRecentlyPlayedGames(@PathVariable Integer playerId) {
        try {
            List<PlayerGameLibrary> recent = libraryService.getRecentlyPlayedGames(playerId);
            return ResponseEntity.ok(recent);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", "获取最近游戏失败",
                    "message", e.getMessage()
            ));
        }
    }

    @GetMapping("/{playerId}/most-played")
    public ResponseEntity<?> getMostPlayedGames(@PathVariable Integer playerId) {
        try {
            List<PlayerGameLibrary> mostPlayed = libraryService.getMostPlayedGames(playerId);
            return ResponseEntity.ok(mostPlayed);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", "获取最常玩游戏失败",
                    "message", e.getMessage()
            ));
        }
    }

    @PutMapping("/{playerId}/games/{gameId}/playtime")
    public ResponseEntity<?> updateGamePlayTime(
            @PathVariable Integer playerId,
            @PathVariable Integer gameId,
            @RequestBody Map<String, Integer> request) {
        try {
            Integer playTime = request.get("playTime");
            PlayerGameLibrary library = libraryService.updateGamePlayTime(playerId, gameId, playTime);
            return ResponseEntity.ok(library);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", "更新游戏时长失败",
                    "message", e.getMessage()
            ));
        }
    }

    @PutMapping("/{playerId}/games/{gameId}/favorite")
    public ResponseEntity<?> toggleFavorite(
            @PathVariable Integer playerId,
            @PathVariable Integer gameId) {
        try {
            PlayerGameLibrary library = libraryService.toggleFavorite(playerId, gameId);
            return ResponseEntity.ok(library);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", "更新收藏状态失败",
                    "message", e.getMessage()
            ));
        }
    }

    @PutMapping("/{playerId}/games/{gameId}/notes")
    public ResponseEntity<?> updateGameNotes(
            @PathVariable Integer playerId,
            @PathVariable Integer gameId,
            @RequestBody Map<String, String> request) {
        try {
            String notes = request.get("notes");
            PlayerGameLibrary library = libraryService.updateGameNotes(playerId, gameId, notes);
            return ResponseEntity.ok(library);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", "更新游戏备注失败",
                    "message", e.getMessage()
            ));
        }
    }

    @DeleteMapping("/{playerId}/games/{gameId}")
    public ResponseEntity<?> removeGameFromLibrary(
            @PathVariable Integer playerId,
            @PathVariable Integer gameId) {
        try {
            libraryService.removeGameFromLibrary(playerId, gameId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", "移除游戏失败",
                    "message", e.getMessage()
            ));
        }
    }
} 