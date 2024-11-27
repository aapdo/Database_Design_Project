package com.drawit.drawit.websocket.test;


import lombok.*;

@Builder
@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
public class ChatMessage {
    private MessageType type;
    private String roomId;
    private String sender;
    private String message;

    public enum MessageType {
        ENTER, TALK
    }
}
