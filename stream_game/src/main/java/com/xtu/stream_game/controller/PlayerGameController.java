package com.xtu.stream_game.controller;

import com.xtu.stream_game.entity.PlayerGame;
import com.xtu.stream_game.entity.Player;
import com.xtu.stream_game.entity.Game;
import com.xtu.stream_game.service.PlayerGameService;
import com.xtu.stream_game.service.PlayerService;
import com.xtu.stream_game.service.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.ArrayList;

@RestController
@RequestMapping("/api/player-games")
public class PlayerGameController {

    @Autowired
    private PlayerGameService playerGameService;
    
    @Autowired
    private PlayerService playerService;
    
    @Autowired
    private GameService gameService;

    // 获取所有玩家游戏记录
    @GetMapping
    public ResponseEntity<List<PlayerGame>> getAllPlayerGames() {
        List<PlayerGame> playerGames = playerGameService.getAllPlayerGames();
        return new ResponseEntity<>(playerGames, HttpStatus.OK);
    }

    // 获取特定玩家的所有游戏
    @GetMapping("/player/{playerId}")
    public ResponseEntity<List<PlayerGame>> getGamesByPlayer(@PathVariable Long playerId) {
        Player player = playerService.getPlayerById(playerId);
        if (player == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        List<PlayerGame> playerGames = playerGameService.getGamesByPlayer(playerId);
        return new ResponseEntity<>(playerGames, HttpStatus.OK);
    }

    // 获取某游戏的所有玩家记录
    @GetMapping("/game/{gameId}")
    public ResponseEntity<List<PlayerGame>> getPlayersByGame(@PathVariable Long gameId) {
        Game game = gameService.getGameById(gameId);
        if (game == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        // 由于Repository不支持直接按游戏查询，此处返回空列表
        return new ResponseEntity<>(new ArrayList<>(), HttpStatus.OK);
    }

    // 获取特定玩家的特定游戏记录
    @GetMapping("/player/{playerId}/game/{gameId}")
    public ResponseEntity<PlayerGame> getPlayerGame(@PathVariable Long playerId, @PathVariable Long gameId) {
        PlayerGame playerGame = playerGameService.getPlayerGame(playerId, gameId);
        if (playerGame == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(playerGame, HttpStatus.OK);
    }

    // 根据购买时间查找玩家游戏
    @GetMapping("/player/{playerId}/purchase-date")
    public ResponseEntity<List<PlayerGame>> getPlayerGamesByPurchaseDate(
            @PathVariable Long playerId,
            @RequestParam String startDate,
            @RequestParam String endDate) {
        // 由于Repository不支持按购买时间查询，此处返回空列表
        return new ResponseEntity<>(new ArrayList<>(), HttpStatus.OK);
    }

    // 根据游戏类型查找玩家游戏
    @GetMapping("/player/{playerId}/game-type/{gameType}")
    public ResponseEntity<List<PlayerGame>> getPlayerGamesByGameType(
            @PathVariable Long playerId, 
            @PathVariable String gameType) {
        List<PlayerGame> playerGames = playerGameService.getPlayerGamesByGameType(playerId, gameType);
        return new ResponseEntity<>(playerGames, HttpStatus.OK);
    }

    // 按照游玩时长排序获取玩家的游戏
    @GetMapping("/player/{playerId}/sort-by-play-time")
    public ResponseEntity<List<PlayerGame>> getPlayerGamesSortedByPlayTime(@PathVariable Long playerId) {
        List<PlayerGame> playerGames = playerGameService.getPlayerGamesSortedByPlayTime(playerId);
        return new ResponseEntity<>(playerGames, HttpStatus.OK);
    }

    // 按照最后游玩时间排序获取玩家的游戏
    @GetMapping("/player/{playerId}/sort-by-last-played")
    public ResponseEntity<List<PlayerGame>> getPlayerGamesSortedByLastPlayed(@PathVariable Long playerId) {
        // 由于Repository不支持按最后游玩时间排序查询，此处返回空列表
        return new ResponseEntity<>(new ArrayList<>(), HttpStatus.OK);
    }

    // 添加玩家游戏记录
    @PostMapping
    public ResponseEntity<PlayerGame> addPlayerGame(@RequestBody PlayerGame playerGame) {
        PlayerGame createdPlayerGame = playerGameService.addPlayerGame(playerGame);
        return new ResponseEntity<>(createdPlayerGame, HttpStatus.CREATED);
    }

    // 更新玩家游戏信息
    @PutMapping("/{id}")
    public ResponseEntity<PlayerGame> updatePlayerGame(@PathVariable Long id, @RequestBody PlayerGame playerGameDetails) {
        PlayerGame updatedPlayerGame = playerGameService.updatePlayerGame(id, playerGameDetails);
        if (updatedPlayerGame == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(updatedPlayerGame, HttpStatus.OK);
    }

    // 移除玩家游戏记录
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> removePlayerGame(@PathVariable Long id) {
        playerGameService.removePlayerGame(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
} 