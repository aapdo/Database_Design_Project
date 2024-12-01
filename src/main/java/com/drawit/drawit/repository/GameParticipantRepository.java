package com.drawit.drawit.repository;

import com.drawit.drawit.entity.GameParticipant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GameParticipantRepository extends JpaRepository<GameParticipant, Long> {

    List<GameParticipant> findByGameRoomId(Long gameRoomId);
    List<GameParticipant> findAllByGameRoomId(Long roomId);

    @Query("""
        SELECT gp
        FROM GameParticipant gp
        JOIN gp.guesses g
        WHERE g.gameRound.id = :gameRoundId
          AND gp.user.nickname = :nickname
    """)
    Optional<GameParticipant> findByGameRoundIdAndUserNickname(@Param("gameRoundId") Long gameRoundId,
                                                              @Param("nickname") String nickname);
}
