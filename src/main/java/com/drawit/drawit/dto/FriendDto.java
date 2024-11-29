package com.drawit.drawit.dto;

import com.drawit.drawit.entity.Friendship;
import com.drawit.drawit.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FriendDto {
    private Long id;
    private Friendship.AcceptStatus status;
    private Long senderId;
    private Long receiverId;
}
