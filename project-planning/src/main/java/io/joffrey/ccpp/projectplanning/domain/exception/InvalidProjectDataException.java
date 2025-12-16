package io.joffrey.ccpp.projectplanning.domain.exception;

import com.ccpp.shared.domain.exception.DomainException;

public class InvalidProjectDataException extends DomainException {

    public InvalidProjectDataException(String message) {
        super(message);
    }
}
