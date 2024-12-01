package com.drawit.drawit.dto.request;

import lombok.Data;

@Data
public class GuessWordRequestDto {
    private String userNickname;
    private Long roundId;
    private String guessedWord;
}