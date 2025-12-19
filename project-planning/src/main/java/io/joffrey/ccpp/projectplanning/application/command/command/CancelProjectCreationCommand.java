package io.joffrey.ccpp.projectplanning.application.command.command;

import com.ccpp.shared.command.Command;
import com.ccpp.shared.identities.ProjectId;

import java.util.UUID;

public record CancelProjectCreationCommand(
        UUID commandId,
        ProjectId projectId,
        String reason,
        UUID correlationId
)  implements Command {

    public CancelProjectCreationCommand(ProjectId projectId, String reason, UUID correlationId) {
        this(UUID.randomUUID(), projectId, reason, correlationId);
    }

    @Override
    public UUID aggregateId() {
        return projectId.value();
    }

    @Override
    public UUID correlationId() {
        return correlationId != null ? correlationId : commandId;
    }

    @Override
    public UUID causationId() {
        return commandId;
    }
}
