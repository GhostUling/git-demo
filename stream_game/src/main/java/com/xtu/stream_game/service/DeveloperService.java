package com.xtu.stream_game.service;

import com.xtu.stream_game.entity.Developer;
import java.util.List;

public interface DeveloperService {
    // 获取所有开发者
    List<Developer> getAllDevelopers();
    
    // 根据ID获取开发者
    Developer getDeveloperById(int developerId);
    
    // 根据名称获取开发者
    Developer getDeveloperByName(String developerName);
    
    // 添加开发者
    Developer addDeveloper(Developer developer);
    
    // 更新开发者信息
    Developer updateDeveloper(int developerId, Developer developerDetails);
    
    // 删除开发者
    void deleteDeveloper(int developerId);
    
    // 开发者登录
    Developer login(String username, String password);
    
    // 获取开发者的游戏销售数据摘要
    List<Object[]> getDeveloperSalesSummary(int developerId);
} 