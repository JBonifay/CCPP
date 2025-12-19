package fr.joffreybonifay.ccpp.projectplanning.application.query.exception;

import fr.joffreybonifay.ccpp.shared.exception.DomainException;
import fr.joffreybonifay.ccpp.shared.identities.ProjectId;

public class ProjectNotFoundException extends DomainException {

    public ProjectNotFoundException(ProjectId projectId) {
        super("Project not found: " + projectId.value());
    }

}
