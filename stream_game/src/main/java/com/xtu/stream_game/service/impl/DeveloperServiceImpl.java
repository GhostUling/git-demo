package com.xtu.stream_game.service.impl;

import com.xtu.stream_game.entity.Developer;
import com.xtu.stream_game.repository.DeveloperRepository;
import com.xtu.stream_game.service.DeveloperService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class DeveloperServiceImpl implements DeveloperService {

    @Autowired
    private DeveloperRepository developerRepository;
    
    @Override
    public List<Developer> getAllDevelopers() {
        return developerRepository.findAll();
    }

    @Override
    public Developer getDeveloperById(int developerId) {
        return developerRepository.findById(developerId).orElse(null);
    }

    @Override
    public Developer getDeveloperByName(String developerName) {
        return developerRepository.findByDeveloperName(developerName);
    }

    @Override
    public Developer addDeveloper(Developer developer) {
        return developerRepository.save(developer);
    }

    @Override
    public Developer updateDeveloper(int developerId, Developer developerDetails) {
        Optional<Developer> developerOpt = developerRepository.findById(developerId);
        
        if (developerOpt.isPresent()) {
            Developer developer = developerOpt.get();
            
            // 更新开发者信息
            if (developerDetails.getDeveloperName() != null) {
                developer.setDeveloperName(developerDetails.getDeveloperName());
            }
            
            if (developerDetails.getContactEmail() != null) {
                developer.setContactEmail(developerDetails.getContactEmail());
            }
            
            return developerRepository.save(developer);
        }
        
        return null;
    }

    @Override
    public void deleteDeveloper(int developerId) {
        developerRepository.deleteById(developerId);
    }

    @Override
    public Developer login(String username, String password) {
        // 注意：我们目前的Developer实体中还没有username和password字段
        // 这里先简单实现登录逻辑，之后需要扩展Developer实体
        return null;
    }

    @Override
    public List<Object[]> getDeveloperSalesSummary(int developerId) {
        // 这个方法需要依赖Game和Transaction相关的信息
        // 现在这些实体和仓库还没有完全实现，先返回空列表
        return new ArrayList<>();
    }
} 