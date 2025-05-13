package com.xtu.stream_game.controller;

import com.xtu.stream_game.entity.Player;
import com.xtu.stream_game.service.EmailService;
import com.xtu.stream_game.service.PlayerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("玩家控制器单元测试")
class PlayerControllerTest {

    private MockMvc mockMvc;

    @Mock
    private PlayerService playerService;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private PlayerController playerController;

    private Player testPlayer;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(playerController).build();
        testPlayer = new Player(1, "testUser", "hashedPassword", "user@example.com");
    }

    //----------------------- 获取玩家 ------------------------
    @Test
    @DisplayName("获取所有玩家 - 成功")
    void getAllPlayers_Success() throws Exception {
        // Given
        List<Player> players = Collections.singletonList(testPlayer);
        when(playerService.getAllPlayers()).thenReturn(players);

        // When & Then
        mockMvc.perform(get("/api/players"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].username", is("testUser")));
    }

    @Test
    @DisplayName("根据ID获取玩家 - 存在")
    void getPlayerById_Exists() throws Exception {
        when(playerService.getPlayerById(1)).thenReturn(testPlayer);

        mockMvc.perform(get("/api/players/1"))
                .andExpect(jsonPath("$.email", is("user@example.com")));
    }

    @Test
    @DisplayName("根据ID获取玩家 - 不存在")
    void getPlayerById_NotExists() throws Exception {
        when(playerService.getPlayerById(999)).thenReturn(null);

        mockMvc.perform(get("/api/players/999"))
                .andExpect(status().isNotFound());
    }

    //----------------------- 验证流程 ------------------------
    @Test
    @DisplayName("发送验证码 - 邮箱未注册")
    void sendVerificationCode_NewEmail() throws Exception {
        // Given
        when(playerService.getPlayerByEmail("new@example.com")).thenReturn(null);
        doNothing().when(emailService).sendVerificationEmail(anyString(), anyString());

        // When & Then
        mockMvc.perform(post("/api/players/send-verification")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"new@example.com\"}"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("发送验证码 - 邮箱已注册")
    void sendVerificationCode_RegisteredEmail() throws Exception {
        when(playerService.getPlayerByEmail("exists@example.com")).thenReturn(testPlayer);

        mockMvc.perform(post("/api/players/send-verification")
                .content("{\"email\":\"exists@example.com\"}"))
                .andExpect(status().isConflict());
    }

    //----------------------- 注册登录 ------------------------
    @Test
    @DisplayName("玩家注册 - 成功")
    void register_Success() throws Exception {
        // Given
        Map<String, Object> request = new HashMap<>();
        request.put("username", "newUser");
        request.put("password", "securePass123");
        request.put("email", "new@example.com");
        request.put("verificationCode", "123456");

        Player newPlayer = new Player(2, "newUser", "encryptedPass", "new@example.com");
        
        when(emailService.verifyEmail("new@example.com", "123456")).thenReturn(true);
        when(playerService.register(any(Player.class))).thenReturn(newPlayer);

        // When & Then
        mockMvc.perform(post("/api/players/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\":\"newUser\",\"password\":\"securePass123\",\"email\":\"new@example.com\",\"verificationCode\":\"123456\"}"))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"));
    }

    @Test
    @DisplayName("玩家登录 - 成功")
    void login_Success() throws Exception {
        when(playerService.login("testUser", "rawPassword")).thenReturn(testPlayer);

        mockMvc.perform(post("/api/players/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\":\"testUser\",\"password\":\"rawPassword\"}"))
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @DisplayName("玩家登录 - 无效凭证")
    void login_InvalidCredentials() throws Exception {
        when(playerService.login("wrongUser", "wrongPass")).thenReturn(null);

        mockMvc.perform(post("/api/players/login")
                .content("{\"username\":\"wrongUser\",\"password\":\"wrongPass\"}"))
                .andExpect(status().isUnauthorized());
    }

    //----------------------- 更新删除 ------------------------
    @Test
    @DisplayName("更新玩家信息 - 成功")
    void updatePlayer_Success() throws Exception {
        Player updatedPlayer = new Player(1, "updatedUser", "newPass", "updated@example.com");
        when(playerService.updatePlayer(1, any())).thenReturn(updatedPlayer);

        mockMvc.perform(put("/api/players/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\":\"updatedUser\",\"email\":\"updated@example.com\"}"))
                .andExpect(jsonPath("$.username").value("updatedUser"));
    }

    @Test
    @DisplayName("删除玩家 - 成功")
    void deletePlayer_Success() throws Exception {
        when(playerService.getPlayerById(1)).thenReturn(testPlayer);
        doNothing().when(playerService).deletePlayer(1);

        mockMvc.perform(delete("/api/players/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("删除玩家 - 不存在")
    void deletePlayer_NotExists() throws Exception {
        when(playerService.getPlayerById(999)).thenReturn(null);

        mockMvc.perform(delete("/api/players/999"))
                .andExpect(status().isNotFound());
    }

    //----------------------- 异常场景 ------------------------
    @Test
    @DisplayName("注册验证失败")
    void register_VerificationFailed() throws Exception {
        when(emailService.verifyEmail(anyString(), anyString())).thenReturn(false);

        mockMvc.perform(post("/api/players/register")
                .content("{\"email\":\"test@example.com\",\"verificationCode\":\"wrong\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("更新不存在的玩家")
    void updatePlayer_NotFound() throws Exception {
        when(playerService.updatePlayer(999, any())).thenReturn(null);

        mockMvc.perform(put("/api/players/999")
                .content("{\"username\":\"ghost\"}"))
                .andExpect(status().isNotFound());
    }
}