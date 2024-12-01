package com.drawit.drawit.controller;

import com.drawit.drawit.dto.GameRoundDto;
import com.drawit.drawit.dto.request.GuessWordRequestDto;
import com.drawit.drawit.dto.request.RequestInviteRoomDto;
import com.drawit.drawit.dto.response.GameGuessResponseDto;
import com.drawit.drawit.service.GameService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.WebSocketSession;

import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@Slf4j
public class GameController {
    private final GameService gameService;
    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/makeRoom")
    // return room number
    public void makeRoom(@Payload Map<String, Object> payload) {
        String userNickname = (String) payload.get("userNickname");
        Map<String, Object> ret = gameService.makeRoom(userNickname);
        // 대기 중인 요청을 클라이언트로 전송
        /*
        log.info("make room. host: " + userNickname);
        log.info("gameRoomId: " + ret.get("gameRoomId"));
        log.info("participantId: " + ret.get("participantId"));
         */
        messagingTemplate.convertAndSend(
                "/queue/roomHost/" + userNickname,
                Map.of(
                        "gameRoomId", ret.get("gameRoomId"),
                        "participantId", ret.get("participantId")
                )
        );

    }

    /**
     * 친구 초대 요청
     */
    @MessageMapping("/inviteRoom")
    public void inviteRoom(@Payload RequestInviteRoomDto requestInviteRoomDto) {
        String hostNickname = requestInviteRoomDto.getHostNickname();
        log.info("초대 보낸 사람 닉네임: "+ hostNickname);

        // 초대 처리
         String receiverNickname = gameService.inviteFriendToRoom(
                hostNickname,
                requestInviteRoomDto.getRoomId(),
                requestInviteRoomDto.getReceiverNickname()
        );
        log.info("초대 받은 사람 닉네임: "+ receiverNickname);

        // 초대 받은 사용자에게 알림
        messagingTemplate.convertAndSend(
                "/queue/inviteRoom/" + receiverNickname ,
                Map.of(
                        "roomId", requestInviteRoomDto.getRoomId(),
                        "hostNickname", hostNickname
                )
        );
    }

    /**
     * 초대 수락
     */
    @MessageMapping("/acceptInvite")
    public void acceptInvite(@Payload Map<String, Object> payload) {
        log.info("초대 수락");
        log.info("payload: " + payload);
        //Long roomId = (Long) payload.get("roomId");
        Long roomId = ((Number)payload.get("roomId")).longValue();
        log.info("방 번호: "+roomId);
        String userNickname = (String) payload.get("userNickname");
        log.info("수락한 유저의 닉네임: "+userNickname);

        // 초대 수락 및 참가자 추가
        Long participantId = gameService.acceptInvite(userNickname, roomId);
        log.info("참가자 아이디: " + participantId);

        // 방 정보 가져오기
        Map<String, Object> roomInfo = gameService.getRoomInfo(roomId);
        log.info("room info: " + roomInfo);
        String hostNickname = (String) roomInfo.get("hostNickname");
        log.info("hostNickname: " + hostNickname);
        List<String> participantNicknameList = (List<String>) roomInfo.get("participantNicknameList");
        log.info("participantNicknameList: " + participantNicknameList);

        for (String nickname : participantNicknameList) {
            messagingTemplate.convertAndSend(
                    "/queue/newParticipant/"+ nickname,
                    Map.of(
                            "roomId", roomId,
                            "participantNicknameList", participantNicknameList,
                            "hostNickname", hostNickname
                    )
            );
        }
    }

    /**
     * 게임 시작 요청
     */
    @MessageMapping("/startGame")
    public void startGame(@Payload Map<String, Object> payload) {
        Long roomId = ((Number) payload.get("roomId")).longValue();

        // 게임 시작 처리
        GameRoundDto gameRoundDto = gameService.startGame(roomId);

        // 모든 참가자에게 게임 시작 알림
        List<String> participantUserNicknameList = gameService.getParticipantUserNicknamesByRoomId(roomId);
        for (String userNickname : participantUserNicknameList) {
            messagingTemplate.convertAndSend(
                    "/game/gameStart/" + userNickname,
                    Map.of(
                            "roomId", roomId,
                            "roundNumber", gameRoundDto.getRoundNumber(),
                            "drawerNickname", gameRoundDto.getDrawerNickname(),
                            "correctWord", gameRoundDto.getCorrectWord()
                    )
            );
        }
    }

    @MessageMapping("/sendImage")
    public void sendPicture(@Payload Map<String, Object> payload) {
        Long roomId = ((Number) payload.get("roomId")).longValue();
        byte[] imageBytes = (byte[]) payload.get("imageData"); // 바이너리 데이터

        List<String> participantUserNicknameList = gameService.getParticipantUserNicknamesByRoomId(roomId);
        for (String userNickname : participantUserNicknameList) {
            messagingTemplate.convertAndSend(
                    "/game/getPicture/" + userNickname,
                    Map.of(
                            "roomId", roomId,
                            // 이미지 데이터가 들어가야함.
                            "image", imageBytes
                    )
            );
        }
    }

    /**
     * 사용자 단어 추측 요청 처리
     */
    @MessageMapping("/guessWord")
    public void guessWord(@Payload GuessWordRequestDto requestDto) {
        // 추측 처리
        GameGuessResponseDto responseDto = gameService.processGuess(
                requestDto.getUserNickname(),
                requestDto.getRoundId(),
                requestDto.getGuessedWord()
        );


        // 결과 클라이언트로 전송
        // similarity가 -1 이면 추측어가 단어장에 없는거임.
        messagingTemplate.convertAndSend(
                "/game/guessResult/"+requestDto.getUserNickname(),
                Map.of(
                        "guessedWord", responseDto.getGuessedWord(),
                        "similarity", responseDto.getSimilarity()
                )
        );
    }


    /**
     *
     * @param payload
     * 이미지를 저장함.
     * 프론트에서는 이걸 호출하고, 정답은 무엇이었고 누가 맞췄는지 또는 누가 가장 유사했는지 알려줘야함..?
     *
     */
    @MessageMapping("/endRound")
    public void endRound(@Payload Map<String, Object> payload) {
        Long gameRoomId = (Long) payload.get("gameRoomId");
        Long gameRoundId = (Long) payload.get("gameRoundId");
        byte[] imageBytes = (byte[]) payload.get("imageData"); // 바이너리 데이터

        String imagePath = gameService.saveImage(gameRoundId, imageBytes);
        Map<String, Map<String, Object>> roundResult = gameService.endRound(gameRoomId, gameRoundId);
        List<String> participantUserNicknameList = gameService.getParticipantUserNicknamesByRoomId(gameRoomId);

        for (String nickname : participantUserNicknameList) {
            messagingTemplate.convertAndSend(
                    "/game/endRound/" + nickname,
                    Map.of(
                            "gameRoomId", gameRoomId,
                            "gameRoundId", gameRoundId,
                            "imagePath", imagePath,
                            "result", roundResult.get(nickname)
                    )
            );
        }
    }

    @MessageMapping("/nextRound")
    public void nextRound(@Payload Map<String, Object> payload) {
        Long roomId = (Long) payload.get("roomId");

        GameRoundDto gameRoundDto = gameService.nextRound(roomId);
        List<String> participantUserNicknameList = gameService.getParticipantUserNicknamesByRoomId(roomId);
        for (String userNickname : participantUserNicknameList) {
            messagingTemplate.convertAndSend(
                    "/game/gameNextRound/" + userNickname,
                    Map.of(
                            "roomId", roomId,
                            "roundNumber", gameRoundDto.getRoundNumber(),
                            "drawerNickname", gameRoundDto.getDrawerNickname(),
                            "correctWord", gameRoundDto.getCorrectWord()
                    )
            );
        }
    }


    @MessageMapping("/endGame")
    public void endGame(@Payload Map<String, Object> payload) {
        Long gameRoomId = (Long) payload.get("gameRoomId");
        // 전체 결과 생성
        Map<String, Object> gameResult = gameService.getGameResult(gameRoomId);

        // 모든 참가자들에게 결과 전송
        List<String> participantUserNicknameList = gameService.getParticipantUserNicknamesByRoomId(gameRoomId);
        for (String nickname : participantUserNicknameList) {
            messagingTemplate.convertAndSend(
                    "/queue/endGame/" + nickname,
                    gameResult
            );
        }
    }



    private Long getUserIdFromSession(WebSocketSession session) {
        return (Long) session.getAttributes().get("userId");
    }
}
