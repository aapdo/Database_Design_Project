package com.drawit.drawit.controller;

import com.drawit.drawit.dto.FriendDto;
import com.drawit.drawit.dto.request.RequestAddFriendDto;
import com.drawit.drawit.dto.request.RequestRespondFriendDto;
import com.drawit.drawit.dto.response.ResponseFriendshipDto;
import com.drawit.drawit.dto.response.ResponseUserDto;
import com.drawit.drawit.entity.Friendship;
import com.drawit.drawit.service.FriendshipService;
import lombok.RequiredArgsConstructor;
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
public class FriendshipController {
    private final FriendshipService friendshipService;
    private final SimpMessagingTemplate messagingTemplate;


    @MessageMapping("/addFriend")
    public void addFriend(RequestAddFriendDto requestAddFriendDto, WebSocketSession session) {
        FriendDto friendDto = friendshipService.addFriend(this.getUserIdFromSession(session), requestAddFriendDto.getReceiverNickname());

        // 요청 수신자에게 실시간으로 메시지 전송
        messagingTemplate.convertAndSendToUser(
                friendDto.getReceiverId().toString(), // 요청 받는 아이디를 가진 클라이언트에게
                "/queue/friendRequests", // 클라이언트 구독 경로 '/user/queue/friendRequests'
                Map.of(
                        "requestId", friendDto.getId(), // 요청 받는 사람이 받는 것: 요청 id
                        "senderNickname", requestAddFriendDto.getSenderNickname()) // + sender nickname
        );
    }

    /**
     * B가 친구 요청을 수락하거나 거절
     */
    @MessageMapping("/respondToFriendRequest")
    public void respondToFriendRequest(RequestRespondFriendDto requestRespondFriendDto) {
        friendshipService.respondToFriendRequest(
                requestRespondFriendDto.getRequestId(),
                requestRespondFriendDto.getStatus()
        );

        // 친구 요청을 보낸 A에게 결과 알림
        messagingTemplate.convertAndSendToUser(
                requestRespondFriendDto.getSenderId().toString(),
                "/queue/friendResponses",
                requestRespondFriendDto
        );
    }

    /**
     * B가 대기 중인 친구 요청을 조회
     */
    @MessageMapping("/getPendingRequests")
    public void getPendingRequests(Long userId) {
        List<ResponseFriendshipDto> pendingRequests = friendshipService.getPendingRequests(userId);

        // 대기 중인 요청을 클라이언트로 전송
        messagingTemplate.convertAndSendToUser(
                userId.toString(),
                "/queue/pendingFriendRequests",
                pendingRequests
        );
    }


    private Long getUserIdFromSession(WebSocketSession session) {
        return (Long) session.getAttributes().get("userId");
    }
}