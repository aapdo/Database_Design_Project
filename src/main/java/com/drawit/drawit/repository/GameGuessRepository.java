package com.drawit.drawit.repository;

import com.drawit.drawit.entity.GameGuess;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GameGuessRepository extends JpaRepository<GameGuess, Long> {
    // 게임 라운드나 참가자로 추측을 조회하는 메서드를 선언할 수 있습니다.
}
