package com.drawit.drawit.dto.response;

import com.drawit.drawit.entity.Friendship;
import com.drawit.drawit.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResponseFriendshipDto {
    private Long id;
    private Friendship.AcceptStatus status;
    private Long senderId;
    private String senderNickname;
    private Long receiverId;
}
