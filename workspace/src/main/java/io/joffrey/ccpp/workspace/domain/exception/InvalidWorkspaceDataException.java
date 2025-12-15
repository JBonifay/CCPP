package io.joffrey.ccpp.workspace.domain.exception;

import com.ccpp.shared.exception.DomainException;

public class InvalidWorkspaceDataException extends DomainException {

    public InvalidWorkspaceDataException(String message) {
        super(message);
    }

}
