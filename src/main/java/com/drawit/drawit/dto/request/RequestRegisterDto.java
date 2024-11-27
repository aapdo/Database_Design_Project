package com.drawit.drawit.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class RequestRegisterDto {
    private String loginId;
    private String password;
    private String nickname;
}
