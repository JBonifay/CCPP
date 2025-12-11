package com.ccpp.shared.exception;

public class ConcurrencyException extends DomainException {
    public ConcurrencyException(String message) {
        super(message);
    }
}
