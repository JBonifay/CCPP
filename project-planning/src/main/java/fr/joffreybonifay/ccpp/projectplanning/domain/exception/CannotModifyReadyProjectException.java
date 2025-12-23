package fr.joffreybonifay.ccpp.projectplanning.domain.exception;

import fr.joffreybonifay.ccpp.shared.domain.exception.DomainException;

public class CannotModifyReadyProjectException extends DomainException {

    public CannotModifyReadyProjectException(String message) {
        super(message);
    }

}
