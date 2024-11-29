package com.drawit.drawit.service;

import com.drawit.drawit.entity.GameParticipant;
import com.drawit.drawit.entity.GameRoom;
import com.drawit.drawit.entity.User;
import com.drawit.drawit.repository.GameParticipantRepository;
import com.drawit.drawit.repository.GameRoomRepository;
import com.drawit.drawit.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class GameService {
    private final UserRepository userRepository;
    private final GameRoomRepository gameRoomRepository;
    private final GameParticipantRepository gameParticipantRepository;

    @Transactional
    public Map<String, Object> makeRoom(Long userId) {
        // 1. 호스트 User 조회
        User host = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));

        // 2. 새로운 GameRoom 생성 및 초기화
        GameRoom newGameRoom = GameRoom.builder()
                .host(host)
                .status(GameRoom.RoomStatus.WAIT)
                .createdAt(LocalDateTime.now())
                .participants(new ArrayList<>()) // 초기화된 리스트
                .build();

        // 3. 호스트를 첫 번째 GameParticipant로 추가
        GameParticipant gameParticipant = GameParticipant.builder()
                .user(host)
                .gameRoom(newGameRoom) // 관계 설정
                .pointsEarned(0) // 초기 점수
                .joinedAt(LocalDateTime.now())
                .build();

        // 4. 양방향 관계 설정
        newGameRoom.getParticipants().add(gameParticipant);

        // 5. 엔티티 저장 (CascadeType 설정에 따라 저장 순서 조정 가능)
        gameRoomRepository.save(newGameRoom);
        gameParticipantRepository.save(gameParticipant);

        // 6. 생성된 GameRoom의 ID 반환
        return Map.of(
                "gameRoomId", newGameRoom.getId(),
                "participantId", gameParticipant.getId());
    }

    public Long joinRoom(Long gameRoomId, Long userId) {
        GameRoom gameRoom = gameRoomRepository.findById(gameRoomId)
                .orElseThrow(() -> new IllegalArgumentException("GameRoom not found with ID: " + gameRoomId));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));

        // 3. GameParticipant 생성
        GameParticipant participant = GameParticipant.builder()
                .user(user)
                .gameRoom(gameRoom)
                .pointsEarned(0) // 초기 점수 설정
                .joinedAt(LocalDateTime.now())
                .build();

        // 4. GameParticipant 저장
        gameParticipantRepository.save(participant);

        List<GameParticipant> participants = gameRoom.getParticipants();
        if (participants == null) {
            gameRoom.setParticipants(new ArrayList<>());
        }

        gameRoom.getParticipants().add(participant);
        gameRoomRepository.save(gameRoom);


        return participant.getId();
    }
}
