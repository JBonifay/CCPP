package fr.joffreybonifay.ccpp.projectplanning.application.command.command;

import fr.joffreybonifay.ccpp.shared.command.Command;
import fr.joffreybonifay.ccpp.shared.domain.identities.ProjectId;

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
    public UUID correlationId() {
        return correlationId != null ? correlationId : commandId;
    }

    @Override
    public UUID causationId() {
        return commandId;
    }
}
