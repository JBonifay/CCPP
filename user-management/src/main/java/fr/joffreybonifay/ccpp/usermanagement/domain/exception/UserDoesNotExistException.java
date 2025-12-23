package fr.joffreybonifay.ccpp.usermanagement.domain.exception;

import fr.joffreybonifay.ccpp.shared.domain.exception.DomainException;

public class UserDoesNotExistException extends DomainException {
    public UserDoesNotExistException(String message) {
        super(message);
    }
}
