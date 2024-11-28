package com.drawit.drawit.dto.response;

import com.drawit.drawit.entity.User;
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
    private LocalDateTime createdAt;

    public ResponseUserDto(User user) {
        this.nickname = user.getNickname();
        this.totalPoint = user.getTotalPoints();
        this.currentPoint = user.getCurrentPoints();
        this.createdAt = user.getCreatedAt();
    }
}
