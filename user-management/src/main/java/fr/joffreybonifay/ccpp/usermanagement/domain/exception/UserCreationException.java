package fr.joffreybonifay.ccpp.usermanagement.domain.exception;

import fr.joffreybonifay.ccpp.shared.exception.DomainException;

public class UserCreationException extends DomainException {

    public UserCreationException(String message) {
        super(message);
    }

}
