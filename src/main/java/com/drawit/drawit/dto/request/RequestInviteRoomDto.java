package com.drawit.drawit.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequestInviteRoomDto {
    private String hostNickname;
    private Long roomId;
    private String receiverNickname;
}
