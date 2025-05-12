package com.xtu.stream_game.repository;

import com.xtu.stream_game.entity.GameUpload;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GameUploadRepository extends JpaRepository<GameUpload, Integer> {
    List<GameUpload> findByDeveloperDeveloperId(Integer developerId);
    List<GameUpload> findByStatus(GameUpload.UploadStatus status);
    List<GameUpload> findByDeveloperDeveloperIdAndStatus(Integer developerId, GameUpload.UploadStatus status);
} 