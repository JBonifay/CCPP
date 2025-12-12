package io.joffrey.ccpp.projectplanning.application.query.exception;

import com.ccpp.shared.exception.DomainException;
import com.ccpp.shared.identities.ProjectId;

public class ProjectNotFoundException extends DomainException {

    public ProjectNotFoundException(ProjectId projectId) {
        super("Project not found: " + projectId.value());
    }

}
