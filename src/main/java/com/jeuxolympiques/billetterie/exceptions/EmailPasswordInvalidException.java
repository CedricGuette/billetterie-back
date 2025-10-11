package com.jeuxolympiques.billetterie.exceptions;

public class EmailPasswordInvalidException extends RuntimeException {
    public EmailPasswordInvalidException(String message) {
        super(message);
    }
}
