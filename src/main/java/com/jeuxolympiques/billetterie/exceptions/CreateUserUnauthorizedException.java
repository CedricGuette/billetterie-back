package com.jeuxolympiques.billetterie.exceptions;

public class CreateUserUnauthorizedException extends RuntimeException {
    public CreateUserUnauthorizedException(String message) {
        super(message);
    }
}
