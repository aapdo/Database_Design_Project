package com.drawit.drawit.dto.request;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class RequestRegisterDto {
    private String loginId;
    private String password;
    private String nickname;
}
