package io.joffrey.ccpp.workspace.domain.exception;

import com.ccpp.shared.domain.exception.DomainException;

public class InvalidWorkspaceDataException extends DomainException {

    public InvalidWorkspaceDataException(String message) {
        super(message);
    }

}
