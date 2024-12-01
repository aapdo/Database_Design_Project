package com.drawit.drawit.service;

import com.drawit.drawit.dto.GameRoundDto;
import com.drawit.drawit.dto.response.GameGuessResponseDto;
import com.drawit.drawit.entity.*;
import com.drawit.drawit.repository.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class GameService {
    private final UserRepository userRepository;
    private final GameRoomRepository gameRoomRepository;
    private final GameParticipantRepository gameParticipantRepository;
    private final GameRoundRepository gameRoundRepository;
    private final GameGuessRepository gameGuessRepository;
    private final HttpRequestService httpRequestService;
    @Value("${python.server.base-url}")
    private String pythonBaseUrl; // application.properties에서 값 주입
    @Transactional
    public Map<String, Object> makeRoom(String userNickname) {
        // 1. 호스트 User 조회
        User host = userRepository.findByNickname(userNickname)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userNickname));

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
    @Transactional
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
    @Transactional
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
    @Transactional
    public List<String> getParticipantUserNicknamesByRoomId(Long roomId) {
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
        // 게임 룸 가져오기
        GameRoom gameRoom = gameRoomRepository.findById(gameRoomId)
                .orElseThrow(() -> new IllegalArgumentException("Game room not found"));

        // 참가자 확인
        List<GameParticipant> participants = gameRoom.getParticipants();
        if (participants.isEmpty()) {
            throw new IllegalStateException("No participants in the room");
        }

        // Drawer 선정
        GameParticipant drawerParticipant = participants.stream()
                .filter(p -> !p.getIsDraw()) // 아직 Drawer가 아닌 참가자
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No eligible drawer available"));

        drawerParticipant.setIsDraw(true);
        gameParticipantRepository.save(drawerParticipant);

        // 랜덤 단어 가져오기
        log.info("랜덤 단어 가져오기 시작");
        String url = pythonBaseUrl + "/randomWord";
        String correctWord;
        try {
            log.info("send get ");
            ResponseEntity<String> response = httpRequestService.sendGetRequest(url);
            log.info("send get ok");

            if (response.getStatusCode() != HttpStatus.OK) {
                throw new RuntimeException("Failed to fetch random word from similarity service");
            }

            // Flask 응답에서 랜덤 단어 추출
            log.info("response body: " + response.getBody());
            Map<String, Object> responseBody = new ObjectMapper().readValue(response.getBody(), Map.class);
            correctWord = responseBody.get("randomWord").toString();
            log.info("랜덤 단어: " +  correctWord);
        } catch (Exception e) {
            throw new RuntimeException("Error fetching random word", e);
        }

        // 새 게임 라운드 생성
        GameRound gameRound = new GameRound();
        gameRound.setDrawer(drawerParticipant.getUser());
        gameRound.setGameRoom(gameRoom);
        gameRound.setRoundNumber(gameRoom.getGameRounds().size() + 1);
        gameRound.setCorrectWord(correctWord);
        gameRound.setImageUrl(""); // 초기값으로 설정
        gameRound.setStartedAt(LocalDateTime.now());
        gameRoundRepository.save(gameRound);

        // DTO 반환
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
    public GameGuessResponseDto processGuess(String userNickname, Long roundId, String guessedWord) {
        // GameRound와 정답 단어 가져오기
        GameRound gameRound = gameRoundRepository.findById(roundId)
                .orElseThrow(() -> new IllegalArgumentException("Game round not found"));

        String correctWord = gameRound.getCorrectWord();

        // GameParticipant 확인
        GameParticipant participant = gameParticipantRepository.findByGameRoundIdAndUserNickname(roundId, userNickname)
                .orElseThrow(() -> new IllegalArgumentException("Participant not found"));

        // Flask의 /getSimilarity 호출
        String url = pythonBaseUrl + "/getSimilarity?correctWord=" + correctWord + "&guessedWord=" + guessedWord;
        ResponseEntity<String> response;

        log.info("유사도 추출 시작");
        try {
            response = httpRequestService.sendGetRequest(url);
            log.info("response");
        } catch (Exception e) {
            throw new RuntimeException("Failed to communicate with similarity service", e);
        }

        double similarity;
        // 상태 코드 확인
        int statusCode = response.getStatusCodeValue();
        log.info("status code: "+ statusCode);
        if (statusCode == 501) {
            similarity = -1;
        } else if (statusCode == 500) {
            throw new RuntimeException("Error occurred while calculating similarity");
        } else if (statusCode != 200) {
            throw new RuntimeException("Unexpected error from similarity service: " + statusCode);
        }

        // 유사도 값 파싱
        try {
            Map<String, Object> responseBody = new ObjectMapper().readValue(response.getBody(), Map.class);
            similarity = Double.parseDouble(responseBody.get("similarity").toString());
            log.info("similarity: "+ similarity);
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse similarity response", e);
        }

        // GameGuess 생성 및 저장
        GameGuess gameGuess = GameGuess.builder()
                .gameRound(gameRound)
                .participant(participant)
                .guessedWord(guessedWord)
                .similarity(similarity)
                .build();

        gameGuessRepository.save(gameGuess);

        // 응답 데이터 생성
        return GameGuessResponseDto.builder()
                .guessedWord(guessedWord)
                .similarity(similarity)
                .build();
    }

    @Transactional
    public String saveImage(Long gameRoundId, byte[] imageBytes) {
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

        return filePath;
    }

    @Transactional
    public Map<String, Map<String, Object>> endRound(Long gameRoomId, Long gameRoundId) {
        // 라운드와 관련된 정보 가져오기
        GameRound gameRound = gameRoundRepository.findById(gameRoundId)
                .orElseThrow(() -> new IllegalArgumentException("GameRound not found"));

        List<GameGuess> guesses = gameGuessRepository.findByGameRoundId(gameRoundId);
        List<GameParticipant> participants = gameParticipantRepository.findByGameRoomId(gameRoomId);

        // 참가자의 nickname -> {guessedWord, similarity, totalPoints} 매핑 결과
        Map<String, Map<String, Object>> result = new HashMap<>();

        // 참가자 점수 업데이트
        for (GameParticipant participant : participants) {
            // 참가자의 가장 유사한 단어 찾기
            GameGuess bestGuess = guesses.stream()
                    .filter(guess -> guess.getParticipant().getId().equals(participant.getId()))
                    .max(Comparator.comparing(GameGuess::getSimilarity))
                    .orElse(null);

            double points = 0;
            String bestGuessedWord = null;
            double bestSimilarity = 0;

            if (bestGuess != null) {
                points = bestGuess.getSimilarity() * 100;
                bestGuessedWord = bestGuess.getGuessedWord();
                bestSimilarity = bestGuess.getSimilarity();
            }

            // 참가자의 총 점수 업데이트
            participant.setPointsEarned(participant.getPointsEarned() + (int) points);
            gameParticipantRepository.save(participant);

            // 결과 저장
            Map<String, Object> guessResult = new HashMap<>();
            guessResult.put("guessedWord", bestGuessedWord);
            guessResult.put("similarity", bestSimilarity);
            guessResult.put("totalPoints", participant.getPointsEarned());

            result.put(participant.getUser().getNickname(), guessResult);
        }

        return result;
    }

    @Transactional
    public Map<String, Object> getGameResult(Long gameRoomId) {
        // 게임 방의 모든 라운드 가져오기
        List<GameRound> gameRounds = gameRoundRepository.findByGameRoomId(gameRoomId);

        // 게임 결과 데이터
        List<Map<String, Object>> roundResults = new ArrayList<>();

        for (GameRound round : gameRounds) {
            // 그림을 그린 사람 닉네임
            String drawerNickname = round.getDrawer().getNickname();

            // 라운드 참가자들의 추측 중 가장 유사도가 높은 답
            List<GameGuess> guesses = round.getGuesses();
            Map<String, Map<String, Object>> participantResults = new HashMap<>();

            for (GameGuess guess : guesses) {
                String participantNickname = guess.getParticipant().getUser().getNickname();

                // 현재 참가자의 가장 유사도가 높은 단어 찾기
                participantResults.computeIfAbsent(participantNickname, k -> new HashMap<>());
                if (participantResults.get(participantNickname).isEmpty() ||
                        (Double) participantResults.get(participantNickname).get("similarity") < guess.getSimilarity()) {
                    participantResults.get(participantNickname).put("guessedWord", guess.getGuessedWord());
                    participantResults.get(participantNickname).put("similarity", guess.getSimilarity());
                }
            }

            // 라운드 결과 저장
            roundResults.add(Map.of(
                    "roundNumber", round.getRoundNumber(),
                    "drawerNickname", drawerNickname,
                    "imageURL", round.getImageUrl(),
                    "participantResults", participantResults
            ));
        }

        // 게임 전체 결과 반환
        return Map.of(
                "gameRoomId", gameRoomId,
                "roundResults", roundResults
        );
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
