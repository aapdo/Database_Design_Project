package com.drawit.drawit.repository;

import com.drawit.drawit.entity.GameRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GameRoomRepository extends JpaRepository<GameRoom, Long> {
    // 추가적인 사용자 정의 메서드를 선언할 수 있습니다.
}
