package com.drawit.drawit.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor @Builder
public class ResponseDto<T> {
    String message;
    T data;
}
