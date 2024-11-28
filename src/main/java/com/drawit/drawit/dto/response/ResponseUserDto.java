package com.drawit.drawit.dto.response;

import com.drawit.drawit.dto.UserDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Data
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ResponseUserDto {
    private String nickname;
    private Integer totalPoint;
    private Integer currentPoint;
    private String nicknameColor;
    private String chattingColor;
    private LocalDateTime createdAt;

    public ResponseUserDto(UserDto user) {
        this.nickname = user.getNickname();
        this.totalPoint = user.getTotalPoints();
        this.currentPoint = user.getCurrentPoints();
        this.nicknameColor = user.getNicknameColor();
        this.chattingColor = user.getChattingColor();
        this.createdAt = user.getCreatedAt();
    }
}
