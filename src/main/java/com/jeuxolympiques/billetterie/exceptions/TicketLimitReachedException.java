package com.jeuxolympiques.billetterie.exceptions;

public class TicketLimitReachedException extends RuntimeException {
    public TicketLimitReachedException(String message) {
        super(message);
    }
}
