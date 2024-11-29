package com.drawit.drawit.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RequestPostDto {
    private Long gameRoundId;
    private Long userId;
    private String imageUrl;
    private String visibility; // 예: "public", "private"
}
