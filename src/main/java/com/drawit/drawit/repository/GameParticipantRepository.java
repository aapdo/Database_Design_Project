package com.drawit.drawit.repository;

import com.drawit.drawit.entity.GameParticipant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GameParticipantRepository extends JpaRepository<GameParticipant, Long> {
    // 게임 방이나 사용자로 참가자 정보를 조회하는 메서드를 선언할 수 있습니다.
}
