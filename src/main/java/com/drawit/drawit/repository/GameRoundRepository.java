package com.drawit.drawit.repository;

import com.drawit.drawit.entity.GameRound;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GameRoundRepository extends JpaRepository<GameRound, Long> {
    // 게임 방 ID로 게임 라운드를 조회하는 메서드 등을 선언할 수 있습니다.
}
