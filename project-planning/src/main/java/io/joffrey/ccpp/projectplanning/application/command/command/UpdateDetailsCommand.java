package io.joffrey.ccpp.projectplanning.application.command.command;

import com.ccpp.shared.command.Command;
import com.ccpp.shared.identities.ProjectId;

import java.util.UUID;

public record UpdateDetailsCommand(
        UUID commandId,
        ProjectId projectId,
        String title,
        String description,
        UUID correlationId
)  implements Command {

    public UpdateDetailsCommand(ProjectId projectId, String title, String description, UUID correlationId) {
        this(UUID.randomUUID(), projectId, title, description, correlationId);
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
