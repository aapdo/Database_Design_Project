package com.drawit.drawit.repository;

import com.drawit.drawit.entity.GameGuess;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GameGuessRepository extends JpaRepository<GameGuess, Long> {
    List<GameGuess> findByGameRoundId(Long gameRoundId);

}
