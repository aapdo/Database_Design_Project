package com.drawit.drawit.dto.request;

import com.drawit.drawit.entity.Friendship;
import lombok.Data;

@Data
public class RequestRespondFriendDto {
    private Long requestId;
    private Long senderId;
    private Friendship.AcceptStatus status; // ACCEPT or REJECT
}