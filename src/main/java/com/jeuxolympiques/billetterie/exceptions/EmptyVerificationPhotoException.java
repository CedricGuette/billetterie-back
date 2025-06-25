package com.jeuxolympiques.billetterie.exceptions;

public class EmptyVerificationPhotoException extends RuntimeException {
    public EmptyVerificationPhotoException(String message) {
        super(message);
    }
}
