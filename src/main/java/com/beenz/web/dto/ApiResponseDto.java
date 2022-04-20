package com.beenz.web.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ApiResponseDto<T> {

    private int status;
    private String message;
    private LocalDateTime time;
    private T data;

    public ApiResponseDto(int status, String message, T data) {
        this.status = status;
        this.message = message;
        this.time = LocalDateTime.now();
        this.data = data;
    }
}
