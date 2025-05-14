package com.xtu.stream_game.service.impl;

import com.xtu.stream_game.entity.GameUpload;
import com.xtu.stream_game.entity.Developer;
import com.xtu.stream_game.entity.Game;
import com.xtu.stream_game.repository.GameUploadRepository;
import com.xtu.stream_game.repository.DeveloperRepository;
import com.xtu.stream_game.repository.GameRepository;
import com.xtu.stream_game.service.GameUploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class GameUploadServiceImpl implements GameUploadService {

    @Autowired
    private GameUploadRepository gameUploadRepository;
    
    @Autowired
    private DeveloperRepository developerRepository;
    
    @Autowired
    private GameRepository gameRepository;

    @Value("${game.upload.path}")
    private String uploadPath;

    @Override
    public GameUpload uploadGame(Integer developerId, String gameName, String version, String description, MultipartFile file) {
        try {
            // 获取开发者
            Developer developer = developerRepository.findById(developerId)
                    .orElseThrow(() -> new RuntimeException("开发者不存在"));

            // 创建上传目录
            Path uploadDir = Paths.get(uploadPath);
            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
            }

            // 生成唯一文件名
            String originalFilename = file.getOriginalFilename();
            if (originalFilename == null || originalFilename.isEmpty()) {
                throw new RuntimeException("文件名不能为空");
            }
            String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String uniqueFileName = UUID.randomUUID().toString() + fileExtension;

            // 保存文件
            Path filePath = uploadDir.resolve(uniqueFileName);
            Files.copy(file.getInputStream(), filePath);

            // 创建上传记录
            GameUpload upload = new GameUpload();
            upload.setDeveloper(developer);
            upload.setGameName(gameName);
            upload.setVersion(version);
            upload.setDescription(description);
            upload.setFilePath(filePath.toString());
            upload.setFileName(originalFilename);
            upload.setFileSize(file.getSize());
            upload.setFileType(file.getContentType());
            
            // 初始时不设置gameId，只有审核通过时才会设置
            upload.setGameId(null);

            return gameUploadRepository.save(upload);
        } catch (IOException e) {
            throw new RuntimeException("文件上传失败: " + e.getMessage());
        }
    }

    @Override
    public List<GameUpload> getDeveloperUploads(Integer developerId) {
        return gameUploadRepository.findByDeveloperDeveloperId(developerId);
    }

    @Override
    public List<GameUpload> getPendingUploads() {
        return gameUploadRepository.findByStatus(GameUpload.UploadStatus.PENDING);
    }

    @Override
    public GameUpload reviewUpload(Integer uploadId, boolean approved, String comment) {
        GameUpload upload = gameUploadRepository.findById(uploadId)
                .orElseThrow(() -> new RuntimeException("上传记录不存在"));

        upload.setStatus(approved ? GameUpload.UploadStatus.APPROVED : GameUpload.UploadStatus.REJECTED);
        upload.setDescription(upload.getDescription() + "\n审核意见: " + comment);

        // 如果审核通过，创建或更新Game实体
        if (approved) {
            try {
                // 首先检查是否已存在同名游戏
                Game existingGame = gameRepository.findByGameName(upload.getGameName());
                
                if (existingGame != null) {
                    // 如果已存在，更新游戏的安装包路径
                    existingGame.setInstallPackagePath(upload.getFilePath());
                    gameRepository.save(existingGame);
                    
                    // 更新上传记录关联的游戏ID
                    upload.setGameId(existingGame.getGameId());
                } else {
                    // 如果不存在，创建新的游戏记录
                    Game newGame = new Game();
                    newGame.setGameName(upload.getGameName());
                    newGame.setDescription(upload.getDescription());
                    
                    // 设置开发者
                    newGame.setDeveloper(upload.getDeveloper());
                    
                    // 设置安装包路径
                    newGame.setInstallPackagePath(upload.getFilePath());
                    
                    // 设置默认价格
                    newGame.setPrice(new BigDecimal("99.00"));
                    
                    // 保存新游戏
                    Game savedGame = gameRepository.save(newGame);
                    
                    // 更新上传记录关联的游戏ID
                    upload.setGameId(savedGame.getGameId());
                }
            } catch (Exception e) {
                // 记录错误但继续处理，不影响审核结果
                System.err.println("创建游戏记录失败: " + e.getMessage());
            }
        }

        return gameUploadRepository.save(upload);
    }

    @Override
    public GameUpload getUploadDetails(Integer uploadId) {
        return gameUploadRepository.findById(uploadId)
                .orElseThrow(() -> new RuntimeException("上传记录不存在"));
    }
} 