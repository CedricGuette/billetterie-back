package com.jeuxolympiques.billetterie.exceptions;

public class CustomerAndTicketNotMatchException extends RuntimeException {
    public CustomerAndTicketNotMatchException(String message) {
        super(message);
    }
}
