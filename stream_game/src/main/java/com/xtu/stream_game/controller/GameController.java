package com.xtu.stream_game.controller;

import com.xtu.stream_game.entity.Game;
import com.xtu.stream_game.service.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/games")
public class GameController {

    @Autowired
    private GameService gameService;

    // 获取所有游戏
    @GetMapping
    public ResponseEntity<List<Game>> getAllGames() {
        List<Game> games = gameService.getAllGames();
        return new ResponseEntity<>(games, HttpStatus.OK);
    }

    // 根据ID获取游戏
    @GetMapping("/{gameId}")
    public ResponseEntity<Game> getGameById(@PathVariable Integer gameId) {
        Game game = gameService.getGameById(gameId);
        if (game == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(game, HttpStatus.OK);
    }

    // 根据名称获取游戏
    @GetMapping("/search")
    public ResponseEntity<Game> getGameByName(@RequestParam String name) {
        Game game = gameService.getGameByName(name);
        if (game == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(game, HttpStatus.OK);
    }

    // 根据开发者ID获取游戏
    @GetMapping("/developer/{developerId}")
    public ResponseEntity<List<Game>> getGamesByDeveloperId(@PathVariable Integer developerId) {
        List<Game> games = gameService.getGamesByDeveloperId(developerId);
        return new ResponseEntity<>(games, HttpStatus.OK);
    }

    // 根据游戏类型获取游戏
    @GetMapping("/type/{gameType}")
    public ResponseEntity<List<Game>> getGamesByType(@PathVariable String gameType) {
        List<Game> games = gameService.getGamesByType(gameType);
        return new ResponseEntity<>(games, HttpStatus.OK);
    }

    // 添加游戏
    @PostMapping
    public ResponseEntity<Game> addGame(@RequestBody Game game) {
        Game createdGame = gameService.addGame(game);
        return new ResponseEntity<>(createdGame, HttpStatus.CREATED);
    }

    // 更新游戏信息
    @PutMapping("/{gameId}")
    public ResponseEntity<Game> updateGame(@PathVariable Integer gameId, @RequestBody Game gameDetails) {
        Game updatedGame = gameService.updateGame(gameId, gameDetails);
        if (updatedGame == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(updatedGame, HttpStatus.OK);
    }

    // 删除游戏
    @DeleteMapping("/{gameId}")
    public ResponseEntity<Void> deleteGame(@PathVariable Integer gameId) {
        Game game = gameService.getGameById(gameId);
        if (game == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        gameService.deleteGame(gameId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
} 