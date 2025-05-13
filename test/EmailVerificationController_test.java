package com.xtu.stream_game.controller;

import com.xtu.stream_game.service.EmailService;
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

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("邮箱验证控制器单元测试")
class EmailVerificationControllerTest {

    private MockMvc mockMvc;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private EmailVerificationController controller;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    //----------------------- 发送验证码 ------------------------
    @Test
    @DisplayName("发送验证码 - 成功")
    void sendCode_Success() throws Exception {
        // Given
        doNothing().when(emailService).sendVerificationEmail(anyString(), anyString());

        // When & Then
        mockMvc.perform(post("/api/email-verification/send-code")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"user@example.com\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("验证码已发送到您的邮箱"));

        verify(emailService).generateVerificationCode();
        verify(emailService).sendVerificationEmail(anyString(), anyString());
    }

    @Test
    @DisplayName("发送验证码 - 空邮箱")
    void sendCode_EmptyEmail() throws Exception {
        mockMvc.perform(post("/api/email-verification/send-code")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("邮箱地址不能为空"));
    }

    @Test
    @DisplayName("发送验证码 - 邮件服务异常")
    void sendCode_ServiceException() throws Exception {
        // Given
        doThrow(new RuntimeException("SMTP连接失败"))
                .when(emailService).sendVerificationEmail(anyString(), anyString());

        // When & Then
        mockMvc.perform(post("/api/email-verification/send-code")
                .content("{\"email\":\"user@example.com\"}")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("发送验证码失败：SMTP连接失败"));
    }

    //----------------------- 验证验证码 ------------------------
    @Test
    @DisplayName("验证验证码 - 成功")
    void verifyCode_Success() throws Exception {
        // Given
        when(emailService.verifyEmail("user@example.com", "123456")).thenReturn(true);

        // When & Then
        mockMvc.perform(post("/api/email-verification/verify")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"user@example.com\",\"code\":\"123456\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.verified").value(true))
                .andExpect(jsonPath("$.message").value("验证成功"));
    }

    @Test
    @DisplayName("验证验证码 - 无效验证码")
    void verifyCode_InvalidCode() throws Exception {
        // Given
        when(emailService.verifyEmail("user@example.com", "wrong")).thenReturn(false);

        // When & Then
        mockMvc.perform(post("/api/email-verification/verify")
                .content("{\"email\":\"user@example.com\",\"code\":\"wrong\"}")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.verified").value(false))
                .andExpect(jsonPath("$.message").value("验证码无效或已过期"));
    }

    @Test
    @DisplayName("验证验证码 - 缺少参数")
    void verifyCode_MissingParams() throws Exception {
        mockMvc.perform(post("/api/email-verification/verify")
                .content("{\"email\":\"user@example.com\"}")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("邮箱和验证码不能为空"));
    }

    //----------------------- 验证状态查询 ------------------------
    @Test
    @DisplayName("查询验证状态 - 已验证")
    void getStatus_Verified() throws Exception {
        // Given
        when(emailService.isEmailVerified("verified@example.com")).thenReturn(true);

        // When & Then
        mockMvc.perform(get("/api/email-verification/status/verified@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.verified").value(true));
    }

    @Test
    @DisplayName("查询验证状态 - 未验证")
    void getStatus_NotVerified() throws Exception {
        // Given
        when(emailService.isEmailVerified("new@example.com")).thenReturn(false);

        // When & Then
        mockMvc.perform(get("/api/email-verification/status/new@example.com"))
                .andExpect(jsonPath("$.verified").value(false));
    }

    @Test
    @DisplayName("查询验证状态 - 服务异常")
    void getStatus_ServiceException() throws Exception {
        // Given
        when(emailService.isEmailVerified("error@example.com"))
                .thenThrow(new RuntimeException("数据库连接失败"));

        // When & Then
        mockMvc.perform(get("/api/email-verification/status/error@example.com"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("获取验证状态失败：数据库连接失败"));
    }
}