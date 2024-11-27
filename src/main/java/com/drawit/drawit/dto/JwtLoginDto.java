package com.drawit.drawit.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class JwtLoginDto {
    private String accessToken;
    private Long userId;
    private String loginId;
    private String nickname;
}