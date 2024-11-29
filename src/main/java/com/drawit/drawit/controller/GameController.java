package com.drawit.drawit.controller;

import com.drawit.drawit.dto.request.RequestJoinRoomDto;
import com.drawit.drawit.service.GameService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.WebSocketSession;

import java.util.Map;

@Controller
@RequiredArgsConstructor
public class GameController {
    private final GameService gameService;
    @MessageMapping("/message")
    @SendTo("/game/response")
    public String handleMessage(String message) {
        return "Server received: " + message;
    }


    @MessageMapping("/makeRoom")
    @SendTo("/game/host")
    // return room number
    public Map<String, Object> makeRoom(WebSocketSession session) {
        Long userId = this.getUserIdFromSession(session);

        return gameService.makeRoom(userId);

    }

    @MessageMapping("/joinRoom")
    @SendTo("/game/participant")
    public Map<String, Object> joinRoom(@Payload RequestJoinRoomDto requestJoinRoomDto, WebSocketSession session) {
        Long userId = this.getUserIdFromSession(session);
        return Map.of(
                "participantId", gameService.joinRoom(requestJoinRoomDto.getGameRoomId(), userId));
    }

    private Long getUserIdFromSession(WebSocketSession session) {
        return (Long) session.getAttributes().get("userId");
    }
}
