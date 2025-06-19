package com.jeuxolympiques.billetterie.exceptions;

public class VerificationPhotoNotFoundException extends RuntimeException {
    public VerificationPhotoNotFoundException(String message) {
        super(message);
    }
}
