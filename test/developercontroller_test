package com.xtu.stream_game.controller;

import com.xtu.stream_game.entity.Developer;
import com.xtu.stream_game.service.DeveloperService;
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

import java.util.Arrays;
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
@DisplayName("开发者控制器单元测试")
class DeveloperControllerTest {

    private MockMvc mockMvc;

    @Mock
    private DeveloperService developerService;

    @InjectMocks
    private DeveloperController developerController;

    private Developer testDeveloper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(developerController).build();
        testDeveloper = new Developer(1, "CD Projekt Red", "contact@cdprojektred.com", "admin", "password123");
    }

    //----------------------- 获取所有开发者 ------------------------
    @Test
    @DisplayName("获取所有开发者 - 成功返回列表")
    void getAllDevelopers_ReturnsList() throws Exception {
        // Given
        List<Developer> developers = Arrays.asList(
            testDeveloper,
            new Developer(2, "Ubisoft", "contact@ubisoft.com", "user", "password456")
        );
        when(developerService.getAllDevelopers()).thenReturn(developers);

        // When & Then
        mockMvc.perform(get("/api/developers"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$", hasSize(2)))
               .andExpect(jsonPath("$[0].name", is("CD Projekt Red")));
    }

    @Test
    @DisplayName("获取所有开发者 - 空列表")
    void getAllDevelopers_EmptyList() throws Exception {
        when(developerService.getAllDevelopers()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/developers"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$", empty()));
    }

    //----------------------- 根据ID获取开发者 ------------------------
    @Test
    @DisplayName("根据ID获取开发者 - 存在")
    void getDeveloperById_Exists() throws Exception {
        when(developerService.getDeveloperById(1)).thenReturn(testDeveloper);

        mockMvc.perform(get("/api/developers/1"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.id", is(1)))
               .andExpect(jsonPath("$.email", is("contact@cdprojektred.com")));
    }

    @Test
    @DisplayName("根据ID获取开发者 - 不存在")
    void getDeveloperById_NotExists() throws Exception {
        when(developerService.getDeveloperById(999)).thenReturn(null);

        mockMvc.perform(get("/api/developers/999"))
               .andExpect(status().isNotFound());
    }

    //----------------------- 根据名称搜索开发者 ------------------------
    @Test
    @DisplayName("根据名称搜索开发者 - 存在")
    void getDeveloperByName_Exists() throws Exception {
        when(developerService.getDeveloperByName("CD Projekt Red")).thenReturn(testDeveloper);

        mockMvc.perform(get("/api/developers/search")
               .param("name", "CD Projekt Red"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.name", is("CD Projekt Red")));
    }

    @Test
    @DisplayName("根据名称搜索开发者 - 不存在")
    void getDeveloperByName_NotExists() throws Exception {
        when(developerService.getDeveloperByName("Unknown Studio")).thenReturn(null);

        mockMvc.perform(get("/api/developers/search")
               .param("name", "Unknown Studio"))
               .andExpect(status().isNotFound());
    }

    //----------------------- 添加开发者 ------------------------
    @Test
    @DisplayName("添加开发者 - 成功")
    void addDeveloper_Success() throws Exception {
        when(developerService.addDeveloper(any(Developer.class))).thenReturn(testDeveloper);

        mockMvc.perform(post("/api/developers")
               .contentType(MediaType.APPLICATION_JSON)
               .content("{\"name\":\"CD Projekt Red\",\"email\":\"contact@cdprojektred.com\"}"))
               .andExpect(status().isCreated())
               .andExpect(header().string("Location", containsString("/api/developers/1")));
    }

    //----------------------- 更新开发者 ------------------------
    @Test
    @DisplayName("更新开发者 - 成功")
    void updateDeveloper_Success() throws Exception {
        Developer updated = new Developer(1, "New Name", "new@email.com", "admin", "newpass");
        when(developerService.updateDeveloper(eq(1), any(Developer.class))).thenReturn(updated);

        mockMvc.perform(put("/api/developers/1")
               .contentType(MediaType.APPLICATION_JSON)
               .content("{\"name\":\"New Name\",\"email\":\"new@email.com\"}"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.name", is("New Name")));
    }

    @Test
    @DisplayName("更新开发者 - ID不存在")
    void updateDeveloper_NotFound() throws Exception {
        when(developerService.updateDeveloper(eq(999), any(Developer.class))).thenReturn(null);

        mockMvc.perform(put("/api/developers/999")
               .contentType(MediaType.APPLICATION_JSON)
               .content("{}"))
               .andExpect(status().isNotFound());
    }

    //----------------------- 删除开发者 ------------------------
    @Test
    @DisplayName("删除开发者 - 成功")
    void deleteDeveloper_Success() throws Exception {
        when(developerService.getDeveloperById(1)).thenReturn(testDeveloper);
        doNothing().when(developerService).deleteDeveloper(1);

        mockMvc.perform(delete("/api/developers/1"))
               .andExpect(status().isNoContent());

        verify(developerService).deleteDeveloper(1);
    }

    @Test
    @DisplayName("删除开发者 - ID不存在")
    void deleteDeveloper_NotFound() throws Exception {
        when(developerService.getDeveloperById(999)).thenReturn(null);

        mockMvc.perform(delete("/api/developers/999"))
               .andExpect(status().isNotFound());

        verify(developerService, never()).deleteDeveloper(anyInt());
    }

    //----------------------- 开发者登录 ------------------------
    @Test
    @DisplayName("开发者登录 - 成功")
    void login_Success() throws Exception {
        Map<String, String> credentials = new HashMap<>();
        credentials.put("username", "admin");
        credentials.put("password", "password123");
        when(developerService.login("admin", "password123")).thenReturn(testDeveloper);

        mockMvc.perform(post("/api/developers/login")
               .contentType(MediaType.APPLICATION_JSON)
               .content("{\"username\":\"admin\",\"password\":\"password123\"}"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.username", is("admin")));
    }

    @Test
    @DisplayName("开发者登录 - 无效凭证")
    void login_InvalidCredentials() throws Exception {
        when(developerService.login("wrong", "wrong")).thenReturn(null);

        mockMvc.perform(post("/api/developers/login")
               .contentType(MediaType.APPLICATION_JSON)
               .content("{\"username\":\"wrong\",\"password\":\"wrong\"}"))
               .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("开发者登录 - 缺少参数")
    void login_MissingParams() throws Exception {
        mockMvc.perform(post("/api/developers/login")
               .contentType(MediaType.APPLICATION_JSON)
               .content("{\"username\":\"admin\"}"))
               .andExpect(status().isBadRequest());
    }

    //----------------------- 销售摘要 ------------------------
    @Test
    @DisplayName("获取销售摘要 - 存在数据")
    void getSalesSummary_WithData() throws Exception {
        List<Object[]> mockData = Arrays.asList(
            new Object[]{"The Witcher 3", 5000000L},
            new Object[]{"Cyberpunk 2077", 3000000L}
        );
        when(developerService.getDeveloperSalesSummary(1)).thenReturn(mockData);

        mockMvc.perform(get("/api/developers/1/sales-summary"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$", hasSize(2)))
               .andExpect(jsonPath("$[0][0]", is("The Witcher 3")));
    }

    @Test
    @DisplayName("获取销售摘要 - 空数据")
    void getSalesSummary_Empty() throws Exception {
        when(developerService.getDeveloperSalesSummary(1)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/developers/1/sales-summary"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$", empty()));
    }
}