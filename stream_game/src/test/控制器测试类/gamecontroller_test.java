package com.xtu.stream_game.controller;

import com.xtu.stream_game.entity.Game;
import com.xtu.stream_game.service.GameService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("游戏控制器单元测试")
class GameControllerTest {

    private MockMvc mockMvc;

    @Mock
    private GameService gameService;

    @InjectMocks
    private GameController gameController;

    private Game testGame;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(gameController).build();
        testGame = new Game(1L, "The Witcher 3", "RPG", 149.99, 1L);
    }

    //----------------------- 获取所有游戏 ------------------------
    @Test
    @DisplayName("获取所有游戏 - 成功返回列表")
    void getAllGames_ReturnsList() throws Exception {
        // Given
        List<Game> games = Arrays.asList(
            testGame,
            new Game(2L, "Cyberpunk 2077", "RPG", 299.0, 1L)
        );
        when(gameService.getAllGames()).thenReturn(games);

        // When & Then
        mockMvc.perform(get("/api/games"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$", hasSize(2)))
               .andExpect(jsonPath("$[0].name", is("The Witcher 3")));
    }

    @Test
    @DisplayName("获取所有游戏 - 空列表")
    void getAllGames_EmptyList() throws Exception {
        when(gameService.getAllGames()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/games"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$", empty()));
    }

    //----------------------- 根据ID获取游戏 ------------------------
    @Test
    @DisplayName("根据ID获取游戏 - 存在")
    void getGameById_Exists() throws Exception {
        when(gameService.getGameById(1L)).thenReturn(testGame);

        mockMvc.perform(get("/api/games/1"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.id", is(1)))
               .andExpect(jsonPath("$.price", is(149.99)));
    }

    @Test
    @DisplayName("根据ID获取游戏 - 不存在")
    void getGameById_NotExists() throws Exception {
        when(gameService.getGameById(999L)).thenReturn(null);

        mockMvc.perform(get("/api/games/999"))
               .andExpect(status().isNotFound());
    }

    //----------------------- 根据名称搜索游戏 ------------------------
    @Test
    @DisplayName("根据名称搜索游戏 - 存在")
    void getGameByName_Exists() throws Exception {
        when(gameService.getGameByName("The Witcher 3")).thenReturn(testGame);

        mockMvc.perform(get("/api/games/search")
               .param("name", "The Witcher 3"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.name", is("The Witcher 3")));
    }

    @Test
    @DisplayName("根据名称搜索游戏 - 不存在")
    void getGameByName_NotExists() throws Exception {
        when(gameService.getGameByName("Unknown Game")).thenReturn(null);

        mockMvc.perform(get("/api/games/search")
               .param("name", "Unknown Game"))
               .andExpect(status().isNotFound());
    }

    //----------------------- 根据开发者获取游戏 ------------------------
    @Test
    @DisplayName("根据开发者ID获取游戏 - 有数据")
    void getGamesByDeveloperId_WithData() throws Exception {
        List<Game> games = Collections.singletonList(testGame);
        when(gameService.getGamesByDeveloperId(1L)).thenReturn(games);

        mockMvc.perform(get("/api/games/developer/1"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$", hasSize(1)))
               .andExpect(jsonPath("$[0].developerId", is(1)));
    }

    @Test
    @DisplayName("根据开发者ID获取游戏 - 无数据")
    void getGamesByDeveloperId_Empty() throws Exception {
        when(gameService.getGamesByDeveloperId(999L)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/games/developer/999"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$", empty()));
    }

    //----------------------- 根据类型获取游戏 ------------------------
    @Test
    @DisplayName("根据类型获取游戏 - 有数据")
    void getGamesByType_WithData() throws Exception {
        List<Game> games = Arrays.asList(
            testGame,
            new Game(2L, "Divinity: Original Sin 2", "RPG", 129.0, 2L)
        );
        when(gameService.getGamesByType("RPG")).thenReturn(games);

        mockMvc.perform(get("/api/games/type/RPG"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$", hasSize(2)))
               .andExpect(jsonPath("$[0].type", is("RPG")));
    }

    //----------------------- 添加游戏 ------------------------
    @Test
    @DisplayName("添加游戏 - 成功")
    void addGame_Success() throws Exception {
        when(gameService.addGame(any(Game.class))).thenReturn(testGame);

        mockMvc.perform(post("/api/games")
               .contentType(MediaType.APPLICATION_JSON)
               .content("{\"name\":\"The Witcher 3\",\"type\":\"RPG\",\"price\":149.99,\"developerId\":1}"))
               .andExpect(status().isCreated())
               .andExpect(header().exists("Location"))
               .andExpect(jsonPath("$.id", is(1)));
    }

    //----------------------- 更新游戏 ------------------------
    @Test
    @DisplayName("更新游戏 - 成功")
    void updateGame_Success() throws Exception {
        Game updatedGame = new Game(1L, "The Witcher 3: Enhanced Edition", "RPG", 199.0, 1L);
        when(gameService.updateGame(eq(1L), any(Game.class))).thenReturn(updatedGame);

        mockMvc.perform(put("/api/games/1")
               .contentType(MediaType.APPLICATION_JSON)
               .content("{\"name\":\"The Witcher 3: Enhanced Edition\",\"price\":199.0}"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.name", is("The Witcher 3: Enhanced Edition")));
    }

    @Test
    @DisplayName("更新游戏 - ID不存在")
    void updateGame_NotFound() throws Exception {
        when(gameService.updateGame(eq(999L), any(Game.class))).thenReturn(null);

        mockMvc.perform(put("/api/games/999")
               .contentType(MediaType.APPLICATION_JSON)
               .content("{}"))
               .andExpect(status().isNotFound());
    }

    //----------------------- 删除游戏 ------------------------
    @Test
    @DisplayName("删除游戏 - 成功")
    void deleteGame_Success() throws Exception {
        when(gameService.getGameById(1L)).thenReturn(testGame);
        doNothing().when(gameService).deleteGame(1L);

        mockMvc.perform(delete("/api/games/1"))
               .andExpect(status().isNoContent());

        verify(gameService).deleteGame(1L);
    }

    @Test
    @DisplayName("删除游戏 - ID不存在")
    void deleteGame_NotFound() throws Exception {
        when(gameService.getGameById(999L)).thenReturn(null);

        mockMvc.perform(delete("/api/games/999"))
               .andExpect(status().isNotFound());

        verify(gameService, never()).deleteGame(anyLong());
    }
}