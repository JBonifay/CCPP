package fr.joffreybonifay.ccpp.workspace.domain.exception;

import fr.joffreybonifay.ccpp.shared.domain.exception.DomainException;

public class WorkspaceDoesNotExistException extends DomainException {

    public WorkspaceDoesNotExistException(String message) {
        super(message);
    }

}
