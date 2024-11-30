package com.drawit.drawit.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class UserListDto {
    private String nickname;
    private Integer totalPoints;
    private Integer currentPoints;
    private String nicknameColor;
    private String chattingColor;
    private LocalDateTime createdAt;
    private Boolean isFriend;

}
