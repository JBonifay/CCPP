package fr.joffreybonifay.ccpp.projectplanning.application.query.exception;

import fr.joffreybonifay.ccpp.shared.domain.exception.DomainException;
import fr.joffreybonifay.ccpp.shared.domain.identities.ProjectId;

public class ProjectNotFoundException extends DomainException {

    public ProjectNotFoundException(ProjectId projectId) {
        super("Project not found: " + projectId.value());
    }

}
