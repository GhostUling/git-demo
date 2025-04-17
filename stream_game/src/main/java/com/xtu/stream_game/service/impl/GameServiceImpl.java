package com.xtu.stream_game.service.impl;

import com.xtu.stream_game.entity.Game;
import com.xtu.stream_game.entity.Developer;
import com.xtu.stream_game.repository.GameRepository;
import com.xtu.stream_game.repository.DeveloperRepository;
import com.xtu.stream_game.service.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class GameServiceImpl implements GameService {

    @Autowired
    private GameRepository gameRepository;
    
    @Autowired
    private DeveloperRepository developerRepository;

    @Override
    public List<Game> getAllGames() {
        return gameRepository.findAll();
    }

    @Override
    public Game getGameById(int gameId) {
        return gameRepository.findById(gameId).orElse(null);
    }

    @Override
    public Game getGameByName(String gameName) {
        return gameRepository.findByGameName(gameName);
    }

    @Override
    public List<Game> getGamesByDeveloper(Developer developer) {
        return gameRepository.findByDeveloper(developer);
    }

    @Override
    public List<Game> getGamesByDeveloperId(int developerId) {
        return gameRepository.findByDeveloperDeveloperId(developerId);
    }

    @Override
    public List<Game> getGamesByType(String gameType) {
        return gameRepository.findByGameType(gameType);
    }

    @Override
    public Game addGame(Game game) {
        // 确保开发者存在
        if (game.getDeveloper() != null && game.getDeveloper().getDeveloperId() > 0) {
            Optional<Developer> developer = developerRepository.findById(game.getDeveloper().getDeveloperId());
            if (developer.isPresent()) {
                game.setDeveloper(developer.get());
            }
        }
        return gameRepository.save(game);
    }

    @Override
    public Game updateGame(int gameId, Game gameDetails) {
        Optional<Game> gameOpt = gameRepository.findById(gameId);
        
        if (gameOpt.isPresent()) {
            Game game = gameOpt.get();
            
            // 更新游戏信息
            if (gameDetails.getGameName() != null) {
                game.setGameName(gameDetails.getGameName());
            }
            
            if (gameDetails.getPrice() != null) {
                game.setPrice(gameDetails.getPrice());
            }
            
            if (gameDetails.getDescription() != null) {
                game.setDescription(gameDetails.getDescription());
            }
            
            if (gameDetails.getInstallPackagePath() != null) {
                game.setInstallPackagePath(gameDetails.getInstallPackagePath());
            }
            
            // 如果更新了开发者，确保该开发者存在
            if (gameDetails.getDeveloper() != null && gameDetails.getDeveloper().getDeveloperId() > 0) {
                Optional<Developer> developer = developerRepository.findById(gameDetails.getDeveloper().getDeveloperId());
                if (developer.isPresent()) {
                    game.setDeveloper(developer.get());
                }
            }
            
            return gameRepository.save(game);
        }
        
        return null;
    }

    @Override
    public void deleteGame(int gameId) {
        gameRepository.deleteById(gameId);
    }
} 