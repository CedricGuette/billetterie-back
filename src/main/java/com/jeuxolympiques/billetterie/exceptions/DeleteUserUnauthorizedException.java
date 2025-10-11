package com.jeuxolympiques.billetterie.exceptions;

public class DeleteUserUnauthorizedException extends RuntimeException {
    public DeleteUserUnauthorizedException(String message) {
        super(message);
    }
}
