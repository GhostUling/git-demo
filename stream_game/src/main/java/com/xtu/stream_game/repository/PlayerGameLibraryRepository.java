package com.xtu.stream_game.repository;

import com.xtu.stream_game.entity.PlayerGameLibrary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlayerGameLibraryRepository extends JpaRepository<PlayerGameLibrary, Long> {
    List<PlayerGameLibrary> findByPlayerId(Long playerId);
    Optional<PlayerGameLibrary> findByPlayerIdAndGameId(Long playerId, Long gameId);
    
    @Query("SELECT pgl FROM PlayerGameLibrary pgl WHERE pgl.player.id = :playerId ORDER BY pgl.lastPlayTime DESC")
    List<PlayerGameLibrary> findByPlayerIdOrderByLastPlayTimeDesc(@Param("playerId") Long playerId);
    
    @Query("SELECT pgl FROM PlayerGameLibrary pgl WHERE pgl.player.id = :playerId ORDER BY pgl.totalPlayTime DESC")
    List<PlayerGameLibrary> findByPlayerIdOrderByTotalPlayTimeDesc(@Param("playerId") Long playerId);
    
    @Query("SELECT pgl FROM PlayerGameLibrary pgl WHERE pgl.player.id = :playerId AND pgl.isInstalled = true")
    List<PlayerGameLibrary> findInstalledGamesByPlayerId(@Param("playerId") Long playerId);
    
    @Query("SELECT pgl FROM PlayerGameLibrary pgl WHERE pgl.player.id = :playerId AND pgl.game.gameType = :gameType")
    List<PlayerGameLibrary> findByPlayerIdAndGameType(@Param("playerId") Long playerId, @Param("gameType") String gameType);
} 