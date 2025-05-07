package com.xtu.stream_game.service;

import com.xtu.stream_game.entity.GameUpload;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface GameUploadService {
    /**
     * 上传游戏
     * @param developerId 开发者ID
     * @param gameName 游戏名称
     * @param version 版本号
     * @param description 描述
     * @param file 游戏文件
     * @return 上传记录
     */
    GameUpload uploadGame(Long developerId, String gameName, String version, String description, MultipartFile file);

    /**
     * 获取开发者的所有上传记录
     * @param developerId 开发者ID
     * @return 上传记录列表
     */
    List<GameUpload> getDeveloperUploads(Long developerId);

    /**
     * 获取所有待审核的上传记录
     * @return 上传记录列表
     */
    List<GameUpload> getPendingUploads();

    /**
     * 审核游戏上传
     * @param uploadId 上传记录ID
     * @param approved 是否通过
     * @param comment 审核意见
     * @return 更新后的上传记录
     */
    GameUpload reviewUpload(Long uploadId, boolean approved, String comment);

    /**
     * 获取上传记录详情
     * @param uploadId 上传记录ID
     * @return 上传记录
     */
    GameUpload getUploadDetails(Long uploadId);
} 