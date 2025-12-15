package io.joffrey.ccpp.projectplanning.application.command.command;

import com.ccpp.shared.command.Command;
import com.ccpp.shared.identities.ProjectId;

public record UpdateProjectDetailsCommand(
        ProjectId projectId,
        String title,
        String description
)  implements Command {
}
