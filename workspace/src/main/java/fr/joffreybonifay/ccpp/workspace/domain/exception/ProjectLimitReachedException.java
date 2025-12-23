package fr.joffreybonifay.ccpp.workspace.domain.exception;

import fr.joffreybonifay.ccpp.shared.domain.exception.DomainException;

public class ProjectLimitReachedException extends DomainException {

    public ProjectLimitReachedException(String message) {
        super(message);
    }

}
