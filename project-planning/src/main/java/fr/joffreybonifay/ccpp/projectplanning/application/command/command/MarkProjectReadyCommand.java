package fr.joffreybonifay.ccpp.projectplanning.application.command.command;

import fr.joffreybonifay.ccpp.shared.command.Command;
import fr.joffreybonifay.ccpp.shared.identities.ProjectId;
import fr.joffreybonifay.ccpp.shared.identities.UserId;

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
    public UUID correlationId() {
        return correlationId != null ? correlationId : commandId;
    }

    @Override
    public UUID causationId() {
        return commandId;
    }
}
