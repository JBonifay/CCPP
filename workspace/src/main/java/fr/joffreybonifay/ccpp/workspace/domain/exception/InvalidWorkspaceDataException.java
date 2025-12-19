package fr.joffreybonifay.ccpp.workspace.domain.exception;

import fr.joffreybonifay.ccpp.shared.exception.DomainException;

public class InvalidWorkspaceDataException extends DomainException {

    public InvalidWorkspaceDataException(String message) {
        super(message);
    }

}
