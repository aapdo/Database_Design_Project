package com.drawit.drawit.repository;

import com.drawit.drawit.entity.GameRound;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GameRoundRepository extends JpaRepository<GameRound, Long> {
    List<GameRound> findByGameRoomId(Long gameRoomId);

}
