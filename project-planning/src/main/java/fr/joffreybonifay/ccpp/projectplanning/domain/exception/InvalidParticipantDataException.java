package fr.joffreybonifay.ccpp.projectplanning.domain.exception;

import fr.joffreybonifay.ccpp.shared.exception.DomainException;

public class InvalidParticipantDataException extends DomainException {
    public InvalidParticipantDataException(String message) {
        super(message);
    }
}
