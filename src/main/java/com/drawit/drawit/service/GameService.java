package com.drawit.drawit.service;

import com.drawit.drawit.dto.GameRoundDto;
import com.drawit.drawit.dto.response.GameGuessResponseDto;
import com.drawit.drawit.entity.*;
import com.drawit.drawit.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
    private final GameRoundRepository gameRoundRepository;
    private final GameGuessRepository gameGuessRepository;
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
                .isDraw(false)
                .build();

        // 4. 양방향 관계 설정
        newGameRoom.getParticipants().add(gameParticipant);

        // 5. 엔티티 저장 (CascadeType 설정에 따라 저장 순서 조정 가능)
        gameRoomRepository.save(newGameRoom);
        gameParticipantRepository.save(gameParticipant);

        // 6. 생성된 GameRoom의 ID 반환
        return Map.of(
                "gameRoomId", newGameRoom.getId(),
                "participantId", gameParticipant.getId(),
                "hostNickname", host.getNickname()
        );
    }

    /**
     * @return receiver nickname
     */
    public String inviteFriendToRoom(String hostNickname, Long roomId, String receiverNickname) {
        GameRoom gameRoom = gameRoomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("Game room not found"));

        if (!gameRoom.getHost().getNickname().equals(hostNickname)) {
            throw new IllegalArgumentException("Only the host can invite friends");
        }

        User receiver = userRepository.findByNickname(receiverNickname)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        return receiver.getNickname();
    }

    /**
     * 초대 수락 및 방 참여
     */
    @Transactional
    public Long acceptInvite(String userNickname, Long roomId) {
        GameRoom gameRoom = gameRoomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("Game room not found"));

        User user = userRepository.findByNickname(userNickname)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        GameParticipant participant = GameParticipant.builder()
                .user(user)
                .gameRoom(gameRoom)
                .pointsEarned(0)
                .joinedAt(LocalDateTime.now())
                .isDraw(false)
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

        List<String> participantNicknameList = gameParticipantRepository.findAllByGameRoomId(roomId).stream()
                .map(participant -> participant.getUser().getNickname())
                .collect(Collectors.toList());

        Map<String, Object> roomInfo = new HashMap<>();
        roomInfo.put("hostNickname", hostNickname);
        roomInfo.put("participantNicknameList", participantNicknameList);

        return roomInfo;
    }

    /**
     * 특정 방의 모든 참가자의 User nickname 목록 가져오기
     */
    public List<String> getParticipantUserIdsByRoomId(Long roomId) {
        return gameParticipantRepository.findAllByGameRoomId(roomId).stream()
                .map(participant -> participant.getUser().getNickname())
                .collect(Collectors.toList());
    }

    /**
     * 게임 시작 처리
     */
    @Transactional
    public GameRoundDto startGame(Long roomId) {
        // GameRoom 조회
        GameRoom gameRoom = gameRoomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("Game room not found"));


        // 상태 변경
        gameRoom.setStatus(GameRoom.RoomStatus.START);
        GameRoundDto gameRoundDto = nextRound(gameRoom.getId());

        if (gameRoom.getGameRounds() == null) {
            gameRoom.setGameRounds(new ArrayList<>());
        }

        gameRoom.getGameRounds().add(gameRoundRepository.findById(gameRoundDto.getGameRoundId())
                .orElseThrow(() -> new IllegalArgumentException("Game round not found") ));
        gameRoomRepository.save(gameRoom);

        return gameRoundDto;

    }

    @Transactional
    public GameRoundDto nextRound(Long gameRoomId) {
        GameRoom gameRoom = gameRoomRepository.findById(gameRoomId)
                .orElseThrow(() -> new IllegalArgumentException("Game room not found"));

        List<GameParticipant> participants = gameRoom.getParticipants();
        if (participants.isEmpty()) {
            throw new IllegalStateException("No participants in the room");
        }

        GameRound gameRound = new GameRound();
        GameParticipant drawerParticipant = participants.stream()
                .filter(p -> !p.getIsDraw()) // 아직 Drawer가 아닌 참가자
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No eligible drawer available"));

        gameRound.setDrawer(drawerParticipant.getUser());
        drawerParticipant.setIsDraw(true);
        gameParticipantRepository.save(drawerParticipant);

        String correctWord = "바보";

        gameRound.setGameRoom(gameRoom);
        gameRound.setRoundNumber(gameRoom.getGameRounds().size() + 1);
        gameRound.setCorrectWord(correctWord);
        gameRound.setImageUrl("");
        gameRound.setStartedAt(LocalDateTime.now());
        gameRoundRepository.save(gameRound);

        return GameRoundDto.builder()
                .gameRoundId(gameRound.getId())
                .gameRoomId(gameRoom.getId())
                .drawerNickname(gameRound.getDrawer().getNickname())
                .correctWord(correctWord)
                .roundNumber(gameRound.getRoundNumber())
                .startedAt(gameRound.getStartedAt())
                .build();
    }


    /**
     * 사용자 추측 단어 처리
     */
    @Transactional
    public GameGuessResponseDto processGuess(Long participantId, Long roundId, String guessedWord) {
        // GameRound와 정답 단어 가져오기
        GameRound gameRound = gameRoundRepository.findById(roundId)
                .orElseThrow(() -> new IllegalArgumentException("Game round not found"));

        String correctWord = gameRound.getCorrectWord();

        // GameParticipant 확인
        GameParticipant participant = gameParticipantRepository.findById(participantId)
                .orElseThrow(() -> new IllegalArgumentException("Participant not found"));

        // 유사도 계산 (가정된 함수 사용)
        double similarity = calculateSimilarity(correctWord, guessedWord);

        // 점수 계산
        int pointsEarned = (int) (similarity * 100);

        // GameGuess 생성 및 저장
        GameGuess gameGuess = GameGuess.builder()
                .gameRound(gameRound)
                .participant(participant)
                .guessedWord(guessedWord)
                .similarity(similarity)
                .pointsEarned(pointsEarned)
                .build();

        gameGuessRepository.save(gameGuess);

        // 응답 데이터 생성
        return GameGuessResponseDto.builder()
                .guessedWord(guessedWord)
                .similarity(similarity)
                .pointsEarned(pointsEarned)
                .build();
    }

    public void saveImage(Long gameRoundId, byte[] imageBytes) {
        LocalDateTime localDateTime = LocalDateTime.now();
        // 파일 저장 경로 설정
        String timestamp = localDateTime.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String fileName = "game_round_" + gameRoundId + "_" + timestamp + ".png";
        String filePath = "./drawImages" + fileName; // 저장할 디렉터리 경로
        // 파일 저장
        File file = new File(filePath);
        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(imageBytes);
        } catch (IOException e) {
            throw new RuntimeException("Can't save image");
        }

        GameRound gameRound = gameRoundRepository.findById(gameRoundId)
                .orElseThrow(() -> new IllegalArgumentException("Game round not found"));

        gameRound.setEndedAt(localDateTime);
        gameRound.setImageUrl(filePath);


    }

    private double calculateSimilarity(String correctWord, String guessedWord) {
        // 실제 유사도 계산 함수 구현 또는 외부 라이브러리 호출
        return 0.5;
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
