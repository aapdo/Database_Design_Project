package com.drawit.drawit.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GameGuessResponseDto {
    private String guessedWord;
    private Double similarity;
    private Integer pointsEarned;
}