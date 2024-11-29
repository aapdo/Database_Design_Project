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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    /**
     * 친구 초대
     * @return receiverId
     */
    public Long inviteFriendToRoom(Long hostId, Long roomId, String receiverNickname) {
        GameRoom gameRoom = gameRoomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("Game room not found"));

        if (!gameRoom.getHost().getId().equals(hostId)) {
            throw new IllegalArgumentException("Only the host can invite friends");
        }

        User receiver = userRepository.findByNickname(receiverNickname)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        return receiver.getId();
    }

    /**
     * 초대 수락 및 방 참여
     */
    @Transactional
    public Long acceptInvite(Long userId, Long roomId) {
        GameRoom gameRoom = gameRoomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("Game room not found"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        GameParticipant participant = GameParticipant.builder()
                .user(user)
                .gameRoom(gameRoom)
                .pointsEarned(0)
                .joinedAt(LocalDateTime.now())
                .build();

        gameParticipantRepository.save(participant);
        return participant.getId();
    }

    /**
     * 특정 방의 정보 가져오기 (호스트 닉네임 및 참가자 닉네임 목록)
     */
    public Map<String, Object> getRoomInfo(Long roomId) {
        GameRoom gameRoom = gameRoomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("Game room not found"));

        String hostNickname = gameRoom.getHost().getNickname();

        List<String> participantNicknames = gameParticipantRepository.findAllByGameRoomId(roomId).stream()
                .map(participant -> participant.getUser().getNickname())
                .collect(Collectors.toList());

        Map<String, Object> roomInfo = new HashMap<>();
        roomInfo.put("hostNickname", hostNickname);
        roomInfo.put("participantNicknames", participantNicknames);

        return roomInfo;
    }

    /**
     * 특정 방의 모든 참가자의 User ID 목록 가져오기
     */
    public List<Long> getParticipantUserIdsByRoomId(Long roomId) {
        return gameParticipantRepository.findAllByGameRoomId(roomId).stream()
                .map(participant -> participant.getUser().getId())
                .collect(Collectors.toList());
    }

    /**
     * 사용자 ID로 닉네임 조회
     */
    public String getUserNicknameById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        return user.getNickname();
    }
}
