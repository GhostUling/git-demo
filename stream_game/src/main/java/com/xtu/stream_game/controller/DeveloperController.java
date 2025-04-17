package com.xtu.stream_game.controller;

import com.xtu.stream_game.entity.Developer;
import com.xtu.stream_game.service.DeveloperService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/developers")
public class DeveloperController {

    @Autowired
    private DeveloperService developerService;

    // 获取所有开发者
    @GetMapping
    public ResponseEntity<List<Developer>> getAllDevelopers() {
        List<Developer> developers = developerService.getAllDevelopers();
        return new ResponseEntity<>(developers, HttpStatus.OK);
    }

    // 根据ID获取开发者
    @GetMapping("/{developerId}")
    public ResponseEntity<Developer> getDeveloperById(@PathVariable int developerId) {
        Developer developer = developerService.getDeveloperById(developerId);
        if (developer == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(developer, HttpStatus.OK);
    }

    // 根据名称获取开发者
    @GetMapping("/search")
    public ResponseEntity<Developer> getDeveloperByName(@RequestParam String name) {
        Developer developer = developerService.getDeveloperByName(name);
        if (developer == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(developer, HttpStatus.OK);
    }

    // 添加开发者
    @PostMapping
    public ResponseEntity<Developer> addDeveloper(@RequestBody Developer developer) {
        Developer createdDeveloper = developerService.addDeveloper(developer);
        return new ResponseEntity<>(createdDeveloper, HttpStatus.CREATED);
    }

    // 更新开发者信息
    @PutMapping("/{developerId}")
    public ResponseEntity<Developer> updateDeveloper(@PathVariable int developerId, @RequestBody Developer developerDetails) {
        Developer updatedDeveloper = developerService.updateDeveloper(developerId, developerDetails);
        if (updatedDeveloper == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(updatedDeveloper, HttpStatus.OK);
    }

    // 删除开发者
    @DeleteMapping("/{developerId}")
    public ResponseEntity<Void> deleteDeveloper(@PathVariable int developerId) {
        Developer developer = developerService.getDeveloperById(developerId);
        if (developer == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        developerService.deleteDeveloper(developerId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // 开发者登录
    @PostMapping("/login")
    public ResponseEntity<Developer> login(@RequestBody Map<String, String> credentials) {
        String username = credentials.get("username");
        String password = credentials.get("password");
        
        if (username == null || password == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        
        Developer developer = developerService.login(username, password);
        if (developer == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        
        return new ResponseEntity<>(developer, HttpStatus.OK);
    }

    // 获取开发者的游戏销售数据摘要
    @GetMapping("/{developerId}/sales-summary")
    public ResponseEntity<List<Object[]>> getDeveloperSalesSummary(@PathVariable int developerId) {
        List<Object[]> salesSummary = developerService.getDeveloperSalesSummary(developerId);
        return new ResponseEntity<>(salesSummary, HttpStatus.OK);
    }
} 