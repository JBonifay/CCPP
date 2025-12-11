package io.joffrey.ccpp.projectplanning.application.query;

import com.ccpp.shared.identities.ProjectId;

public record GetProjectByIdQuery(
        ProjectId projectId
) {
}
