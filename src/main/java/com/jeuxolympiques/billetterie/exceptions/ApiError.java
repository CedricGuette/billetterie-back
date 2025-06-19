package com.jeuxolympiques.billetterie.exceptions;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ApiError {
    private String error;
    private int code;
    private LocalDateTime timestamp;
}
