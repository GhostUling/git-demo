package com.xtu.stream_game.controller;

import com.xtu.stream_game.entity.GameUpload;
import com.xtu.stream_game.service.GameUploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.Arrays;

@RestController
@RequestMapping("/api/game-upload")
public class GameUploadController {

    @Autowired
    private GameUploadService gameUploadService;

    @Value("${game.upload.max-size}")
    private String maxFileSize;

    @Value("${game.upload.allowed-types}")
    private String allowedTypes;

    @PostMapping("/upload")
    public ResponseEntity<?> uploadGame(
            @RequestParam("developerId") Long developerId,
            @RequestParam("gameName") String gameName,
            @RequestParam("version") String version,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam("file") MultipartFile file) {
        try {
            // 验证文件大小
            long maxSize = parseFileSize(maxFileSize);
            if (file.getSize() > maxSize) {
                return ResponseEntity.badRequest().body(Map.of(
                        "error", "文件大小超出限制",
                        "message", "最大允许上传 " + maxFileSize
                ));
            }

            // 验证文件类型
            String contentType = file.getContentType();
            if (contentType == null || !Arrays.asList(allowedTypes.split(",")).contains(contentType)) {
                return ResponseEntity.badRequest().body(Map.of(
                        "error", "不支持的文件类型",
                        "message", "只允许上传压缩文件"
                ));
            }

            GameUpload upload = gameUploadService.uploadGame(developerId, gameName, version, description, file);
            return ResponseEntity.ok(upload);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", "上传失败",
                    "message", e.getMessage()
            ));
        }
    }

    @GetMapping("/developer/{developerId}")
    public ResponseEntity<?> getDeveloperUploads(@PathVariable Long developerId) {
        try {
            List<GameUpload> uploads = gameUploadService.getDeveloperUploads(developerId);
            return ResponseEntity.ok(uploads);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", "获取上传记录失败",
                    "message", e.getMessage()
            ));
        }
    }

    @GetMapping("/pending")
    public ResponseEntity<?> getPendingUploads() {
        try {
            List<GameUpload> uploads = gameUploadService.getPendingUploads();
            return ResponseEntity.ok(uploads);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", "获取待审核记录失败",
                    "message", e.getMessage()
            ));
        }
    }

    @PostMapping("/review/{uploadId}")
    public ResponseEntity<?> reviewUpload(
            @PathVariable Long uploadId,
            @RequestBody Map<String, Object> request) {
        try {
            boolean approved = (boolean) request.get("approved");
            String comment = (String) request.get("comment");
            GameUpload upload = gameUploadService.reviewUpload(uploadId, approved, comment);
            return ResponseEntity.ok(upload);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", "审核失败",
                    "message", e.getMessage()
            ));
        }
    }

    @GetMapping("/{uploadId}")
    public ResponseEntity<?> getUploadDetails(@PathVariable Long uploadId) {
        try {
            GameUpload upload = gameUploadService.getUploadDetails(uploadId);
            return ResponseEntity.ok(upload);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", "获取上传详情失败",
                    "message", e.getMessage()
            ));
        }
    }

    private long parseFileSize(String size) {
        size = size.toUpperCase();
        if (size.endsWith("MB")) {
            return Long.parseLong(size.substring(0, size.length() - 2)) * 1024 * 1024;
        } else if (size.endsWith("KB")) {
            return Long.parseLong(size.substring(0, size.length() - 2)) * 1024;
        } else if (size.endsWith("B")) {
            return Long.parseLong(size.substring(0, size.length() - 1));
        }
        return Long.parseLong(size);
    }
} 