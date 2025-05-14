package com.xtu.stream_game.controller;

import com.xtu.stream_game.entity.PlayerGameLibrary;
import com.xtu.stream_game.service.PlayerGameLibraryService;
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
@DisplayName("玩家游戏库控制器单元测试")
class PlayerGameLibraryControllerTest {

    private MockMvc mockMvc;

    @Mock
    private PlayerGameLibraryService libraryService;

    @InjectMocks
    private PlayerGameLibraryController controller;

    private PlayerGameLibrary testLibrary;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
        testLibrary = new PlayerGameLibrary();
        testLibrary.setPlayerId(1);
        testLibrary.setGameId(100);
        testLibrary.setFavorite(true);
        testLibrary.setPlayTime(120);
    }

    //----------------------- 添加游戏到库 ------------------------
    @Test
    @DisplayName("添加游戏到库 - 成功")
    void addGameToLibrary_Success() throws Exception {
        when(libraryService.addGameToLibrary(1, 100)).thenReturn(testLibrary);

        mockMvc.perform(post("/api/library/1/games/100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.gameId").value(100));
    }

    @Test
    @DisplayName("添加游戏到库 - 游戏已存在")
    void addGameToLibrary_Duplicate() throws Exception {
        when(libraryService.addGameToLibrary(1, 100))
            .thenThrow(new RuntimeException("游戏已存在库中"));

        mockMvc.perform(post("/api/library/1/games/100"))
                .andExpect(jsonPath("$.error").value("添加游戏失败"));
    }

    //----------------------- 获取游戏信息 ------------------------
    @Test
    @DisplayName("获取库中游戏详情 - 存在")
    void getGameFromLibrary_Exists() throws Exception {
        when(libraryService.getGameFromLibrary(1, 100)).thenReturn(testLibrary);

        mockMvc.perform(get("/api/library/1/games/100"))
                .andExpect(jsonPath("$.favorite").value(true));
    }

    @Test
    @DisplayName("获取库中游戏详情 - 不存在")
    void getGameFromLibrary_NotExists() throws Exception {
        when(libraryService.getGameFromLibrary(1, 999)).thenReturn(null);

        mockMvc.perform(get("/api/library/1/games/999"))
                .andExpect(status().isBadRequest());
    }

    //----------------------- 获取完整游戏库 ------------------------
    @Test
    @DisplayName("获取玩家完整游戏库 - 有数据")
    void getPlayerLibrary_WithData() throws Exception {
        List<PlayerGameLibrary> list = Arrays.asList(testLibrary);
        when(libraryService.getPlayerLibrary(1)).thenReturn(list);

        mockMvc.perform(get("/api/library/1/games"))
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    @DisplayName("获取玩家完整游戏库 - 空库")
    void getPlayerLibrary_Empty() throws Exception {
        when(libraryService.getPlayerLibrary(2)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/library/2/games"))
                .andExpect(jsonPath("$", empty()));
    }

    //----------------------- 收藏功能测试 ------------------------
    @Test
    @DisplayName("切换收藏状态 - 成功")
    void toggleFavorite_Success() throws Exception {
        testLibrary.setFavorite(false);
        when(libraryService.toggleFavorite(1, 100)).thenReturn(testLibrary);

        mockMvc.perform(put("/api/library/1/games/100/favorite"))
                .andExpect(jsonPath("$.favorite").value(false));
    }

    //----------------------- 游戏时长更新 ------------------------
    @Test
    @DisplayName("更新游戏时长 - 有效参数")
    void updateGamePlayTime_Valid() throws Exception {
        testLibrary.setPlayTime(180);
        when(libraryService.updateGamePlayTime(1, 100, 180)).thenReturn(testLibrary);

        mockMvc.perform(put("/api/library/1/games/100/playtime")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"playTime\":180}"))
                .andExpect(jsonPath("$.playTime").value(180));
    }

    @Test
    @DisplayName("更新游戏时长 - 无效参数")
    void updateGamePlayTime_Invalid() throws Exception {
        mockMvc.perform(put("/api/library/1/games/100/playtime")
                .content("{}")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value("更新游戏时长失败"));
    }

    //----------------------- 游戏备注管理 ------------------------
    @Test
    @DisplayName("更新游戏备注 - 成功")
    void updateGameNotes_Success() throws Exception {
        testLibrary.setNotes("测试备注");
        when(libraryService.updateGameNotes(1, 100, "测试备注")).thenReturn(testLibrary);

        mockMvc.perform(put("/api/library/1/games/100/notes")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"notes\":\"测试备注\"}"))
                .andExpect(jsonPath("$.notes").value("测试备注"));
    }

    //----------------------- 游戏移除测试 ------------------------
    @Test
    @DisplayName("从库中移除游戏 - 成功")
    void removeGameFromLibrary_Success() throws Exception {
        doNothing().when(libraryService).removeGameFromLibrary(1, 100);

        mockMvc.perform(delete("/api/library/1/games/100"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("从库中移除游戏 - 不存在记录")
    void removeGameFromLibrary_NotExists() throws Exception {
        doThrow(new RuntimeException("游戏不在库中"))
            .when(libraryService).removeGameFromLibrary(1, 999);

        mockMvc.perform(delete("/api/library/1/games/999"))
                .andExpect(jsonPath("$.error").value("移除游戏失败"));
    }

    //----------------------- 特殊查询测试 ------------------------
    @Test
    @DisplayName("获取最近游玩游戏 - 有数据")
    void getRecentlyPlayed_WithData() throws Exception {
        List<PlayerGameLibrary> list = Collections.singletonList(testLibrary);
        when(libraryService.getRecentlyPlayedGames(1)).thenReturn(list);

        mockMvc.perform(get("/api/library/1/recent"))
                .andExpect(jsonPath("$[0].gameId").value(100));
    }

    @Test
    @DisplayName("获取最常玩游戏 - 排序验证")
    void getMostPlayed_Sorted() throws Exception {
        PlayerGameLibrary lib1 = new PlayerGameLibrary();
        lib1.setPlayTime(300);
        PlayerGameLibrary lib2 = new PlayerGameLibrary();
        lib2.setPlayTime(150);
        
        when(libraryService.getMostPlayedGames(1))
            .thenReturn(Arrays.asList(lib1, lib2));

        mockMvc.perform(get("/api/library/1/most-played"))
                .andExpect(jsonPath("$[0].playTime").value(300))
                .andExpect(jsonPath("$[1].playTime").value(150));
    }
}