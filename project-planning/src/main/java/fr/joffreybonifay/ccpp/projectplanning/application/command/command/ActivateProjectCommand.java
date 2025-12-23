package fr.joffreybonifay.ccpp.projectplanning.application.command.command;

import fr.joffreybonifay.ccpp.shared.command.Command;
import fr.joffreybonifay.ccpp.shared.domain.identities.ProjectId;

import java.util.UUID;

public record ActivateProjectCommand(
    UUID commandId,
    ProjectId projectId,
    UUID correlationId,
    UUID causationId
) implements Command {

    public ActivateProjectCommand(ProjectId projectId, UUID correlationId, UUID causationId) {
        this(UUID.randomUUID(), projectId, correlationId, causationId);
    }

    @Override
    public UUID correlationId() {
        return correlationId != null ? correlationId : commandId;
    }

    @Override
    public UUID causationId() {
        return causationId;
    }
}
