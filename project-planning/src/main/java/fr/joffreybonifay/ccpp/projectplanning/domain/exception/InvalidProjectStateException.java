package fr.joffreybonifay.ccpp.projectplanning.domain.exception;

public class InvalidProjectStateException extends RuntimeException {

    public InvalidProjectStateException(String message) {
        super(message);
    }
}
