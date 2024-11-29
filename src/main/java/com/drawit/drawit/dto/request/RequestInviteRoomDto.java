package com.drawit.drawit.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequestInviteRoomDto {
    private Long hostId;
    private Long roomId;
    private String receiverNickname;
}
