package io.joffrey.ccpp.projectplanning.application.command.command;

import com.ccpp.shared.command.Command;
import com.ccpp.shared.identities.ProjectId;
import com.ccpp.shared.identities.UserId;

import java.util.UUID;

public record MarkProjectReadyCommand(
        UUID commandId,
        ProjectId projectId,
        UserId userId,
        UUID correlationId
)  implements Command {

    public MarkProjectReadyCommand(ProjectId projectId, UserId userId, UUID correlationId) {
        this(UUID.randomUUID(), projectId, userId, correlationId);
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
