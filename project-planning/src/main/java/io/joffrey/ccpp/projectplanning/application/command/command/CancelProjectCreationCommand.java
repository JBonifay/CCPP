package io.joffrey.ccpp.projectplanning.application.command.command;

import com.ccpp.shared.infrastructure.command.Command;
import com.ccpp.shared.domain.identities.ProjectId;

public record CancelProjectCreationCommand(
        ProjectId projectId,
        String reason
)  implements Command {
}
