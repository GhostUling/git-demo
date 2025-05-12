package com.xtu.stream_game.repository;

import com.xtu.stream_game.entity.PlayerGameLibrary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlayerGameLibraryRepository extends JpaRepository<PlayerGameLibrary, Integer> {
    List<PlayerGameLibrary> findByPlayerPlayerId(Integer playerId);
    Optional<PlayerGameLibrary> findByPlayerPlayerIdAndGameGameId(Integer playerId, Integer gameId);
    List<PlayerGameLibrary> findByPlayerPlayerIdAndIsFavorite(Integer playerId, Boolean isFavorite);
    List<PlayerGameLibrary> findByPlayerPlayerIdOrderByLastPlayTimeDesc(Integer playerId);
    List<PlayerGameLibrary> findByPlayerPlayerIdOrderByPlayTimeDesc(Integer playerId);
    
    @Query("SELECT pgl FROM PlayerGameLibrary pgl WHERE pgl.player.playerId = :playerId ORDER BY pgl.playTime DESC")
    List<PlayerGameLibrary> findByPlayerIdOrderByPlayTimeDesc(@Param("playerId") Integer playerId);
    
    @Query("SELECT pgl FROM PlayerGameLibrary pgl WHERE pgl.player.playerId = :playerId")
    List<PlayerGameLibrary> findInstalledGamesByPlayerId(@Param("playerId") Integer playerId);
    
    @Query("SELECT pgl FROM PlayerGameLibrary pgl WHERE pgl.player.playerId = :playerId AND pgl.game.gameType = :gameType")
    List<PlayerGameLibrary> findByPlayerIdAndGameType(@Param("playerId") Integer playerId, @Param("gameType") String gameType);

    @Query("SELECT pgl FROM PlayerGameLibrary pgl WHERE pgl.player.playerId = :playerId AND pgl.game.gameId IN " +
            "(SELECT g.gameId FROM Game g WHERE LOWER(g.gameName) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    List<PlayerGameLibrary> findByPlayerIdAndGameTitleContainingIgnoreCase(@Param("playerId") Integer playerId, 
                                                                        @Param("keyword") String keyword);
    
    @Query("SELECT pgl FROM PlayerGameLibrary pgl WHERE pgl.player.playerId = :playerId AND pgl.game.gameId IN " +
            "(SELECT g.gameId FROM Game g WHERE g.gameType = :category)")
    List<PlayerGameLibrary> findByPlayerIdAndGameCategory(@Param("playerId") Integer playerId, 
                                                      @Param("category") String category);
    
    @Query("SELECT pgl FROM PlayerGameLibrary pgl WHERE pgl.player.playerId = :playerId AND pgl.game.gameId IN " +
            "(SELECT g.gameId FROM Game g WHERE g.developer.developerId = :developerId)")
    List<PlayerGameLibrary> findByPlayerIdAndGameDeveloperId(@Param("playerId") Integer playerId, 
                                                         @Param("developerId") Integer developerId);
} 