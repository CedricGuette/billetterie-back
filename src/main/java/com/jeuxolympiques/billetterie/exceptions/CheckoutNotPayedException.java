package com.jeuxolympiques.billetterie.exceptions;

public class CheckoutNotPayedException extends RuntimeException {
    public CheckoutNotPayedException(String message) {
        super(message);
    }
}
