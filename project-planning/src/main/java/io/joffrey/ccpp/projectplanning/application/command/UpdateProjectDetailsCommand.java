package io.joffrey.ccpp.projectplanning.application.command;

import com.ccpp.shared.identities.ProjectId;

public record UpdateProjectDetailsCommand(
        ProjectId projectId,
        String title,
        String description
) {
}
