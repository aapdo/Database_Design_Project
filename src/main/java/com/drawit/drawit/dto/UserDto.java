package com.drawit.drawit.dto;

import com.drawit.drawit.entity.User;
import lombok.*;

import java.time.LocalDateTime;


@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@ToString
public class UserDto {
    private Long id;
    private String roles;
    private String loginId;
    private String password;
    private String nickname;
    private Integer totalPoints = 0;
    private Integer currentPoints = 0;
    private LocalDateTime createdAt = LocalDateTime.now();
    private String nicknameColor = "black";
    private String chattingColor = "black";

    public UserDto(User user) {
        this.id = user.getId();
        this.roles = user.getRoles();
        this.loginId = user.getLoginId();
        this.nickname = user.getNickname();
        this.password = user.getPassword();
        this.totalPoints = user.getTotalPoints();
        this.currentPoints = user.getCurrentPoints();
        this.createdAt = user.getCreatedAt();
        this.nicknameColor = user.getNicknameColor();
        this.chattingColor = user.getChattingColor();
    }
}
