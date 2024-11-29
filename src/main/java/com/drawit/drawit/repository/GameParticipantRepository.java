package com.drawit.drawit.repository;

import com.drawit.drawit.entity.GameParticipant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GameParticipantRepository extends JpaRepository<GameParticipant, Long> {
    List<GameParticipant> findAllByGameRoomId(Long roomId);

}
