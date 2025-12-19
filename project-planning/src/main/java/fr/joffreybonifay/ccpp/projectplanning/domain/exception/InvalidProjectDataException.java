package fr.joffreybonifay.ccpp.projectplanning.domain.exception;

import fr.joffreybonifay.ccpp.shared.exception.DomainException;

public class InvalidProjectDataException extends DomainException {

    public InvalidProjectDataException(String message) {
        super(message);
    }
}
