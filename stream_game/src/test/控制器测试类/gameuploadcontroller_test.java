package com.xtu.stream_game.controller;

import com.xtu.stream_game.entity.GameUpload;
import com.xtu.stream_game.service.GameUploadService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
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
@DisplayName("游戏上传控制器单元测试")
class GameUploadControllerTest {

    private MockMvc mockMvc;

    @Mock
    private GameUploadService gameUploadService;

    @InjectMocks
    private GameUploadController gameUploadController;

    private GameUpload testUpload;

    @BeforeEach
    void setUp() {
        // 初始化模拟MVC环境并注入配置
        mockMvc = MockMvcBuilders.standaloneSetup(gameUploadController)
                .defaultRequest(multipart("/api/game-upload/upload").with(csrf())) // 启用CSRF
                .build();

        testUpload = new GameUpload(1L, 1001L, "The Witcher 3", "1.0.0", 
                "game-upload.zip", "pending", "Initial version");
    }

    //----------------------- 文件上传测试 ------------------------
    @Test
    @DisplayName("上传游戏 - 成功")
    void uploadGame_Success() throws Exception {
        // 配置允许的测试文件类型和大小
        gameUploadController = new GameUploadController();
        gameUploadController.gameUploadService = gameUploadService;
        gameUploadController.maxFileSize = "10MB";
        gameUploadController.allowedTypes = "application/zip,application/x-zip-compressed";

        // 模拟上传文件
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "game.zip",
                "application/zip",
                "test content".getBytes()
        );

        when(gameUploadService.uploadGame(
                anyLong(), anyString(), anyString(), anyString(), any())
        ).thenReturn(testUpload);

        mockMvc.perform(multipart("/api/game-upload/upload")
                .file(file)
                .param("developerId", "1001")
                .param("gameName", "The Witcher 3")
                .param("version", "1.0.0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fileName", is("game-upload.zip")));
    }

    @Test
    @DisplayName("上传游戏 - 文件大小超出限制")
    void uploadGame_FileSizeExceeded() throws Exception {
        gameUploadController.maxFileSize = "1KB";
        MockMultipartFile file = new MockMultipartFile(
                "file", "bigfile.zip", "application/zip", new byte[2048]
        );

        mockMvc.perform(multipart("/api/game-upload/upload")
                .file(file)
                .param("developerId", "1001")
                .param("gameName", "Test Game"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("文件大小超出限制")));
    }

    @Test
    @DisplayName("上传游戏 - 非法文件类型")
    void uploadGame_InvalidFileType() throws Exception {
        gameUploadController.allowedTypes = "application/pdf";
        MockMultipartFile file = new MockMultipartFile(
                "file", "game.zip", "application/zip", "content".getBytes()
        );

        mockMvc.perform(multipart("/api/game-upload/upload")
                .file(file)
                .param("developerId", "1001")
                .param("gameName", "Test Game"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("只允许上传压缩文件")));
    }

    //----------------------- 开发者上传记录 ------------------------
    @Test
    @DisplayName("获取开发者上传记录 - 有数据")
    void getDeveloperUploads_WithData() throws Exception {
        List<GameUpload> uploads = Arrays.asList(
                testUpload,
                new GameUpload(2L, 1001L, "Cyberpunk", "2.0", "game2.zip", "approved", "New version")
        );
        when(gameUploadService.getDeveloperUploads(1001L)).thenReturn(uploads);

        mockMvc.perform(get("/api/game-upload/developer/1001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].status", is("pending")));
    }

    @Test
    @DisplayName("获取开发者上传记录 - 空列表")
    void getDeveloperUploads_Empty() throws Exception {
        when(gameUploadService.getDeveloperUploads(999L)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/game-upload/developer/999"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", empty()));
    }

    //----------------------- 待审核记录 ------------------------
    @Test
    @DisplayName("获取待审核记录 - 成功")
    void getPendingUploads_Success() throws Exception {
        List<GameUpload> pending = Collections.singletonList(testUpload);
        when(gameUploadService.getPendingUploads()).thenReturn(pending);

        mockMvc.perform(get("/api/game-upload/pending"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].status", is("pending")));
    }

    //----------------------- 审核流程 ------------------------
    @Test
    @DisplayName("审核通过 - 成功")
    void reviewUpload_Approve() throws Exception {
        Map<String, Object> request = new HashMap<>();
        request.put("approved", true);
        request.put("comment", "符合规范");

        GameUpload approved = new GameUpload(1L, 1001L, "The Witcher 3", "1.0.0", 
                "game.zip", "approved", "审核通过");
        when(gameUploadService.reviewUpload(1L, true, "符合规范")).thenReturn(approved);

        mockMvc.perform(post("/api/game-upload/review/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"approved\":true,\"comment\":\"符合规范\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("approved")));
    }

    @Test
    @DisplayName("审核拒绝 - 成功")
    void reviewUpload_Reject() throws Exception {
        GameUpload rejected = testUpload;
        rejected.setStatus("rejected");
        when(gameUploadService.reviewUpload(1L, false, "存在违规内容")).thenReturn(rejected);

        mockMvc.perform(post("/api/game-upload/review/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"approved\":false,\"comment\":\"存在违规内容\"}"))
                .andExpect(jsonPath("$.status", is("rejected")));
    }

    //----------------------- 上传详情 ------------------------
    @Test
    @DisplayName("获取上传详情 - 存在")
    void getUploadDetails_Exists() throws Exception {
        when(gameUploadService.getUploadDetails(1L)).thenReturn(testUpload);

        mockMvc.perform(get("/api/game-upload/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.version", is("1.0.0")));
    }

    @Test
    @DisplayName("获取上传详情 - 不存在")
    void getUploadDetails_NotFound() throws Exception {
        when(gameUploadService.getUploadDetails(999L)).thenThrow(new RuntimeException("记录不存在"));

        mockMvc.perform(get("/api/game-upload/999"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("获取上传详情失败")));
    }

    //----------------------- 工具方法测试 ------------------------
    @Test
    @DisplayName("文件大小解析 - MB")
    void parseFileSize_MB() {
        long size = gameUploadController.parseFileSize("10MB");
        assertEquals(10 * 1024 * 1024, size);
    }

    @Test
    @DisplayName("文件大小解析 - KB")
    void parseFileSize_KB() {
        long size = gameUploadController.parseFileSize("500KB");
        assertEquals(500 * 1024, size);
    }

    @Test
    @DisplayName("文件大小解析 - 字节")
    void parseFileSize_Bytes() {
        long size = gameUploadController.parseFileSize("2048B");
        assertEquals(2048, size);
    }
}

