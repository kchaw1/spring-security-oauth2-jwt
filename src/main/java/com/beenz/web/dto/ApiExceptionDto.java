package com.beenz.web.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ApiExceptionDto {

    private int status;
    private String message;
    private LocalDateTime time;

    public ApiExceptionDto(int status, String message) {
        this.status = status;
        this.message = message;
        this.time = LocalDateTime.now();
    }
}
