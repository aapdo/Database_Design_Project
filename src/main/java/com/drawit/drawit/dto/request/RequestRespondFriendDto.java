package com.drawit.drawit.dto.request;

import com.drawit.drawit.entity.Friendship;
import lombok.Data;

@Data
public class RequestRespondFriendDto {
    private Long friendshipId;
    private String senderNickname;
    private String receiverNickname;
    private Friendship.AcceptStatus status; // ACCEPT or REJECT
}