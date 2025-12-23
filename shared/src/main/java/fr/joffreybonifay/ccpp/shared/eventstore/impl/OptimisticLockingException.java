package fr.joffreybonifay.ccpp.shared.eventstore.impl;

public class OptimisticLockingException extends RuntimeException {

    public OptimisticLockingException(String message) {
        super(message);
    }

}
