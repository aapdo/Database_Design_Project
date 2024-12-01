package com.drawit.drawit.controller;

import com.drawit.drawit.dto.GameRoundDto;
import com.drawit.drawit.dto.request.GuessWordRequestDto;
import com.drawit.drawit.dto.request.RequestInviteRoomDto;
import com.drawit.drawit.dto.request.RequestJoinRoomDto;
import com.drawit.drawit.dto.response.GameGuessResponseDto;
import com.drawit.drawit.entity.GameRound;
import com.drawit.drawit.service.GameService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.WebSocketSession;

import java.io.File;
import java.io.FileOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class GameController {
    private final GameService gameService;
    private final SimpMessagingTemplate messagingTemplate;
    @MessageMapping("/message")
    @SendTo("/game/response")
    public String handleMessage(String message) {
        return "Server received: " + message;
    }


    @MessageMapping("/makeRoom")
    // return room number
    public void makeRoom(@Payload Map<String, Object> payload) {
        String userNickname = (String) payload.get("userNickname");
        Map<String, Object> ret = gameService.makeRoom(userNickname);
        // 대기 중인 요청을 클라이언트로 전송
        messagingTemplate.convertAndSend(
                "/roomHost/" + ret.get("hostNickname"),
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

        // 초대 처리
         String receiverNickname = gameService.inviteFriendToRoom(
                hostNickname,
                requestInviteRoomDto.getRoomId(),
                requestInviteRoomDto.getReceiverNickname()
        );

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
        Long roomId = ((Number) payload.get("roomId")).longValue();
        String userNickname = (String) payload.get("userNickname");



        // 초대 수락 및 참가자 추가
        Long participantId = gameService.acceptInvite(userNickname, roomId);

        // 방 정보 가져오기
        Map<String, Object> roomInfo = gameService.getRoomInfo(roomId);
        String hostNickname = (String) roomInfo.get("hostNickname");
        List<String> participantNicknameList = (List<String>) roomInfo.get("participantNicknameList");

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
        List<String> participantUserNicknameList = gameService.getParticipantUserIdsByRoomId(roomId);
        for (String userNickname : participantUserNicknameList) {
            messagingTemplate.convertAndSend(
                    "/queue/gameStart/" + userNickname,
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

        List<String> participantUserNicknameList = gameService.getParticipantUserIdsByRoomId(roomId);
        for (String userNickname : participantUserNicknameList) {
            messagingTemplate.convertAndSend(
                    "/queue/getPicture/" + userNickname,
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
                requestDto.getParticipantId(),
                requestDto.getRoundId(),
                requestDto.getGuessedWord()
        );

        // 결과 클라이언트로 전송
        messagingTemplate.convertAndSendToUser(
                requestDto.getParticipantId().toString(),
                "/queue/guessResult",
                responseDto
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
        Long gameRoundId = (Long) payload.get("gameRoundId");
        byte[] imageBytes = (byte[]) payload.get("imageData"); // 바이너리 데이터

        gameService.saveImage(gameRoundId, imageBytes);
    }

    @MessageMapping("/nextRound")
    public void nextRound(@Payload Map<String, Object> payload) {
        Long roomId = (Long) payload.get("roomId");

        GameRoundDto gameRoundDto = gameService.nextRound(roomId);
        List<String> participantUserNicknameList = gameService.getParticipantUserIdsByRoomId(roomId);
        for (String userNickname : participantUserNicknameList) {
            messagingTemplate.convertAndSend(
                    "/queue/gameNextRound/" + userNickname,
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

    }



    private Long getUserIdFromSession(WebSocketSession session) {
        return (Long) session.getAttributes().get("userId");
    }
}
