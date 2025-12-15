package io.joffrey.ccpp.projectplanning.application.command.command;

import com.ccpp.shared.command.Command;
import com.ccpp.shared.identities.ProjectId;

public record CancelProjectCreationCommand(
        ProjectId projectId,
        String reason
)  implements Command {
}
