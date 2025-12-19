package fr.joffreybonifay.ccpp.shared.exception;

public abstract class DomainException extends RuntimeException {

    public DomainException(String message) {
        super(message);
    }

}
