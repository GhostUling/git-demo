package com.xtu.stream_game.controller;

import com.xtu.stream_game.entity.*;
import com.xtu.stream_game.service.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("玩家游戏记录控制器单元测试")
class PlayerGameControllerTest {

    private MockMvc mockMvc;

    @Mock
    private PlayerGameService playerGameService;

    @Mock
    private PlayerService playerService;

    @Mock
    private GameService gameService;

    @InjectMocks
    private PlayerGameController controller;

    private PlayerGame testRecord;
    private Player testPlayer;
    private Game testGame;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
        
        testPlayer = new Player(1, "player1", "pass123", "player@test.com");
        testGame = new Game(100, "Test Game", "RPG", 99.99, 1);
        testRecord = new PlayerGame(
            1, 
            testPlayer, 
            testGame, 
            LocalDateTime.now(), 
            120, 
            LocalDateTime.now().minusDays(5)
        );
    }

    //----------------------- 基础查询测试 ------------------------
    @Test
    @DisplayName("获取所有玩家游戏记录")
    void getAllPlayerGames_Success() throws Exception {
        when(playerGameService.getAllPlayerGames()).thenReturn(Arrays.asList(testRecord));

        mockMvc.perform(get("/api/player-games"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(1)));
    }

    //----------------------- 玩家维度查询 ------------------------
    @Test
    @DisplayName("获取玩家游戏记录 - 存在玩家")
    void getGamesByPlayer_Exists() throws Exception {
        when(playerService.getPlayerById(1)).thenReturn(testPlayer);
        when(playerGameService.getGamesByPlayer(1)).thenReturn(Collections.singletonList(testRecord));

        mockMvc.perform(get("/api/player-games/player/1"))
                .andExpect(jsonPath("$[0].game.name", is("Test Game")));
    }

    @Test
    @DisplayName("获取玩家游戏记录 - 不存在玩家")
    void getGamesByPlayer_NotExists() throws Exception {
        when(playerService.getPlayerById(999)).thenReturn(null);

        mockMvc.perform(get("/api/player-games/player/999"))
                .andExpect(status().isNotFound());
    }

    //----------------------- 游戏维度查询 ------------------------
    @Test
    @DisplayName("获取游戏玩家记录 - 存在游戏")
    void getPlayersByGame_GameExists() throws Exception {
        when(gameService.getGameById(100)).thenReturn(testGame);

        mockMvc.perform(get("/api/player-games/game/100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", empty()));
    }

    @Test
    @DisplayName("获取游戏玩家记录 - 不存在游戏")
    void getPlayersByGame_GameNotExists() throws Exception {
        when(gameService.getGameById(999)).thenReturn(null);

        mockMvc.perform(get("/api/player-games/game/999"))
                .andExpect(status().isNotFound());
    }

    //----------------------- 复合查询测试 ------------------------
    @Test
    @DisplayName("获取玩家特定游戏记录 - 存在记录")
    void getPlayerGame_Exists() throws Exception {
        when(playerGameService.getPlayerGame(1, 100)).thenReturn(testRecord);

        mockMvc.perform(get("/api/player-games/player/1/game/100"))
                .andExpect(jsonPath("$.purchaseDate").exists());
    }

    @Test
    @DisplayName("获取玩家特定游戏记录 - 不存在记录")
    void getPlayerGame_NotExists() throws Exception {
        when(playerGameService.getPlayerGame(1, 999)).thenReturn(null);

        mockMvc.perform(get("/api/player-games/player/1/game/999"))
                .andExpect(status().isNotFound());
    }

    //----------------------- 条件查询测试 ------------------------
    @Test
    @DisplayName("按游戏类型查询玩家游戏 - 有数据")
    void getPlayerGamesByGameType_WithData() throws Exception {
        when(playerGameService.getPlayerGamesByGameType(1, "RPG"))
            .thenReturn(Collections.singletonList(testRecord));

        mockMvc.perform(get("/api/player-games/player/1/game-type/RPG"))
                .andExpect(jsonPath("$[0].gameType", is("RPG")));
    }

    @Test
    @DisplayName("按游玩时长排序查询")
    void getPlayerGamesSortedByPlayTime() throws Exception {
        when(playerGameService.getPlayerGamesSortedByPlayTime(1))
            .thenReturn(Arrays.asList(testRecord));

        mockMvc.perform(get("/api/player-games/player/1/sort-by-play-time"))
                .andExpect(jsonPath("$[0].playTimeMinutes", is(120)));
    }

    //----------------------- 写入操作测试 ------------------------
    @Test
    @DisplayName("添加玩家游戏记录 - 成功")
    void addPlayerGame_Success() throws Exception {
        when(playerGameService.addPlayerGame(any()))
            .thenReturn(testRecord);

        mockMvc.perform(post("/api/player-games")
                .contentType("application/json")
                .content("{ \"playerId\": 1, \"gameId\": 100 }"))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"));
    }

    @Test
    @DisplayName("更新玩家游戏记录 - 成功")
    void updatePlayerGame_Success() throws Exception {
        testRecord.setPlayTimeMinutes(180);
        when(playerGameService.updatePlayerGame(1, any()))
            .thenReturn(testRecord);

        mockMvc.perform(put("/api/player-games/1")
                .content("{ \"playTimeMinutes\": 180 }")
                .contentType("application/json"))
                .andExpect(jsonPath("$.playTimeMinutes").value(180));
    }

    @Test
    @DisplayName("删除玩家游戏记录 - 成功")
    void removePlayerGame_Success() throws Exception {
        doNothing().when(playerGameService).removePlayerGame(1);

        mockMvc.perform(delete("/api/player-games/1"))
                .andExpect(status().isNoContent());
    }

    //----------------------- 异常场景测试 ------------------------
    @Test
    @DisplayName("更新不存在的记录")
    void updatePlayerGame_NotFound() throws Exception {
        when(playerGameService.updatePlayerGame(999, any()))
            .thenReturn(null);

        mockMvc.perform(put("/api/player-games/999")
                .content("{}")
                .contentType("application/json"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("按类型查询无结果")
    void getPlayerGamesByGameType_Empty() throws Exception {
        when(playerGameService.getPlayerGamesByGameType(1, "FPS"))
            .thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/player-games/player/1/game-type/FPS"))
                .andExpect(jsonPath("$", empty()));
    }
}