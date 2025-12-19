package io.joffrey.ccpp.projectplanning.application.command.command;

import com.ccpp.shared.command.Command;
import com.ccpp.shared.identities.ProjectId;

import java.util.UUID;

public record CancelProjectCreationCommand(
        UUID commandId,
        ProjectId projectId,
        String reason,
        UUID correlationId,
        UUID causationId
)  implements Command {
    @Override
    public UUID aggregateId() {
        return null;
    }
}
