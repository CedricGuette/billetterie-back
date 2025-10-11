package com.jeuxolympiques.billetterie.exceptions;

public class CustomerNotCreatedException extends RuntimeException {
    public CustomerNotCreatedException(String message) {
        super(message);
    }
}
