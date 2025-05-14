package com.xtu.stream_game.service.impl;

import com.xtu.stream_game.entity.GameUpload;
import com.xtu.stream_game.entity.Developer;
import com.xtu.stream_game.entity.Game;
import com.xtu.stream_game.repository.GameUploadRepository;
import com.xtu.stream_game.repository.DeveloperRepository;
import com.xtu.stream_game.repository.GameRepository;
import com.xtu.stream_game.service.GameUploadService;
import com.xtu.stream_game.service.GameService;
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
import java.util.UUID;

@Service
public class GameUploadServiceImpl implements GameUploadService {

    @Autowired
    private GameUploadRepository gameUploadRepository;
    
    @Autowired
    private DeveloperRepository developerRepository;
    
    @Autowired
    private GameRepository gameRepository;
    
    @Autowired
    private GameService gameService;

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
        
        // 如果审核通过，创建游戏记录
        if (approved) {
            try {
                // 查询是否已存在该游戏
                Game existingGame = gameRepository.findByGameName(upload.getGameName());
                
                if (existingGame == null) {
                    // 创建新游戏记录
                    Game game = new Game();
                    game.setGameName(upload.getGameName());
                    game.setDescription(upload.getDescription());
                    game.setDeveloper(upload.getDeveloper());
                    game.setGameType("用户上传");
                    
                    // 设置默认价格（应根据实际情况调整）
                    // 可以从描述中解析价格信息，这里简单设置为99元
                    game.setPrice(new BigDecimal("99.00"));
                    
                    // 设置安装包路径
                    game.setInstallPackagePath(upload.getFilePath());
                    
                    // 保存游戏记录
                    gameService.addGame(game);
                    
                    // 更新上传记录，添加关联的游戏ID
                    upload.setDescription(upload.getDescription() + "\n已创建游戏ID: " + game.getGameId());
                } else {
                    // 如果游戏已存在，更新版本信息
                    existingGame.setInstallPackagePath(upload.getFilePath());
                    gameService.updateGame(existingGame.getGameId(), existingGame);
                    
                    // 更新上传记录，添加关联的游戏ID
                    upload.setDescription(upload.getDescription() + "\n已更新游戏ID: " + existingGame.getGameId());
                }
            } catch (Exception e) {
                // 记录错误，但不阻止审核过程
                upload.setDescription(upload.getDescription() + "\n创建游戏记录失败: " + e.getMessage());
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