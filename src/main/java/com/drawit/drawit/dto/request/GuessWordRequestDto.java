package com.drawit.drawit.dto.request;

import lombok.Data;

@Data
public class GuessWordRequestDto {
    private Long participantId;
    private Long roundId;
    private String guessedWord;
}