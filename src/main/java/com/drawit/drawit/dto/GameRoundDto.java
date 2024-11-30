package com.drawit.drawit.dto;


import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class GameRoundDto {
    private Long gameRoundId;
    private Long gameRoomId;
    private String drawerNickname;
    private Integer roundNumber;
    private LocalDateTime startedAt;
    private String correctWord;
}