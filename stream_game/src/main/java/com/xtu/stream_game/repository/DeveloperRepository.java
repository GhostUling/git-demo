package com.xtu.stream_game.repository;

import com.xtu.stream_game.entity.Developer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DeveloperRepository extends JpaRepository<Developer, Integer> {
    // 根据开发者名称查找开发者
    Developer findByDeveloperName(String developerName);
    
    // 根据联系邮箱查找开发者
    Developer findByContactEmail(String contactEmail);
} 