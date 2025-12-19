package io.joffrey.ccpp.workspace.domain.exception;

import com.ccpp.shared.exception.DomainException;

public class ProjectLimitReachedException extends DomainException {

    public ProjectLimitReachedException(String message) {
        super(message);
    }

}
