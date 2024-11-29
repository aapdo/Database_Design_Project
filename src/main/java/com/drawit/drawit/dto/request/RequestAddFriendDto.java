package com.drawit.drawit.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RequestAddFriendDto {
    private String senderNickname;
    private String receiverNickname;
}
