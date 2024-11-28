package com.drawit.drawit.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationPrincipalDto {
    private Long id;
    private String nickname;
    private String loginId;

}
