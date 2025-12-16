package io.joffrey.ccpp.projectplanning.domain.exception;

import com.ccpp.shared.domain.exception.DomainException;

public class InvalidParticipantDataException extends DomainException {
    public InvalidParticipantDataException(String message) {
        super(message);
    }
}
