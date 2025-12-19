package io.joffrey.ccpp.projectplanning.domain.exception;

import com.ccpp.shared.exception.DomainException;

public class CannotModifyReadyProjectException extends DomainException {

    public CannotModifyReadyProjectException(String message) {
        super(message);
    }

}
