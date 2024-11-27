package com.drawit.drawit.websocket.test2;

import org.springframework.web.socket.TextMessage;
        import org.springframework.web.socket.WebSocketSession;
        import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.springframework.web.socket.sockjs.client.WebSocketClientSockJsSession;
import org.springframework.web.socket.sockjs.client.XhrClientSockJsSession;

import java.util.ArrayList;
import java.util.List;

public class ChatWebSocketHandler extends TextWebSocketHandler {
    // 현재 연결된 세션 목록
    private final List<WebSocketSession> sessions = new ArrayList<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        System.out.println(session instanceof XhrClientSockJsSession);
        System.out.println(session instanceof WebSocketClientSockJsSession);
        System.out.println("session.getClass() = " + session.getClass());
        // 새 연결이 수립되면 세션 추가
        sessions.add(session);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        // 메시지를 받은 경우 모든 클라이언트에게 브로드캐스트
        for (WebSocketSession s : sessions) {
            s.sendMessage(new TextMessage(message.getPayload()));
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, org.springframework.web.socket.CloseStatus status) throws Exception {
        // 연결 종료 시 세션 제거
        sessions.remove(session);
    }
}
