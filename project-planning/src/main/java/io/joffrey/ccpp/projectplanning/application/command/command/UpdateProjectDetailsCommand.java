package io.joffrey.ccpp.projectplanning.application.command.command;

import com.ccpp.shared.infrastructure.command.Command;
import com.ccpp.shared.domain.identities.ProjectId;

public record UpdateProjectDetailsCommand(
        ProjectId projectId,
        String title,
        String description
)  implements Command {
}
