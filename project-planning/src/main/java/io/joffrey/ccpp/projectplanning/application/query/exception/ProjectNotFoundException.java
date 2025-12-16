package io.joffrey.ccpp.projectplanning.application.query.exception;

import com.ccpp.shared.domain.exception.DomainException;
import com.ccpp.shared.domain.identities.ProjectId;

public class ProjectNotFoundException extends DomainException {

    public ProjectNotFoundException(ProjectId projectId) {
        super("Project not found: " + projectId.value());
    }

}
