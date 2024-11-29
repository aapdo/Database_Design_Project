package com.drawit.drawit.controller;

import com.drawit.drawit.dto.request.RequestInviteRoomDto;
import com.drawit.drawit.dto.request.RequestJoinRoomDto;
import com.drawit.drawit.service.GameService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
    public void makeRoom(WebSocketSession session) {
        Long userId = this.getUserIdFromSession(session);
        Map<String, Object> ret = gameService.makeRoom(userId);
        // 대기 중인 요청을 클라이언트로 전송
        messagingTemplate.convertAndSendToUser(
                userId.toString(),
                "/roomHost",
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
    public void inviteRoom(@Payload RequestInviteRoomDto requestInviteRoomDto, WebSocketSession session) {
        Long hostId = getUserIdFromSession(session);

        // 초대 처리
        Long receiverId = gameService.inviteFriendToRoom(
                hostId,
                requestInviteRoomDto.getRoomId(),
                requestInviteRoomDto.getReceiverNickname()
        );

        // 초대 받은 사용자에게 알림
        messagingTemplate.convertAndSendToUser(
                receiverId.toString(),
                "/queue/inviteRoom",
                Map.of(
                        "roomId", requestInviteRoomDto.getRoomId(),
                        "hostId", hostId,
                        "hostNickname", requestInviteRoomDto.getHostId()
                )
        );
    }

    /**
     * 초대 수락
     */
    @MessageMapping("/acceptInvite")
    public void acceptInvite(@Payload Map<String, Object> payload, WebSocketSession session) {
        Long userId = getUserIdFromSession(session);
        Long roomId = ((Number) payload.get("roomId")).longValue();

        // 초대 수락 및 참가자 추가
        Long participantId = gameService.acceptInvite(userId, roomId);

        // 방 정보 가져오기
        Map<String, Object> roomInfo = gameService.getRoomInfo(roomId);
        String hostNickname = (String) roomInfo.get("hostNickname");
        List<String> participantNicknames = (List<String>) roomInfo.get("participantNicknames");
        String newParticipantNickname = gameService.getUserNicknameById(userId);

        // 초대를 수락한 사용자에게 방 정보 전송
        messagingTemplate.convertAndSendToUser(
                userId.toString(),
                "/queue/acceptInvite",
                Map.of(
                        "roomId", roomId,
                        "participantId", participantId,
                        "hostNickname", hostNickname,
                        "participantNicknames", participantNicknames
                )
        );

        // 기존 참가자들에게 새로 들어온 참가자 정보 알림
        List<Long> participantUserIds = gameService.getParticipantUserIdsByRoomId(roomId);
        for (Long participantUserId : participantUserIds) {
            if (!participantUserId.equals(userId)) { // 기존 참가자에게만 전송
                messagingTemplate.convertAndSendToUser(
                        participantUserId.toString(),
                        "/queue/newParticipant",
                        Map.of(
                                "roomId", roomId,
                                "newParticipantNickname", newParticipantNickname
                        )
                );
            }
        }
    }

    private Long getUserIdFromSession(WebSocketSession session) {
        return (Long) session.getAttributes().get("userId");
    }
}
