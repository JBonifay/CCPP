package fr.joffreybonifay.ccpp.workspace.application.command.command;


import fr.joffreybonifay.ccpp.shared.command.Command;
import fr.joffreybonifay.ccpp.shared.identities.WorkspaceId;

import java.util.UUID;

public record ApproveProjectCreationCommand(
        UUID commandId,
        WorkspaceId workspaceId,
        UUID correlationId
) implements Command {

    public ApproveProjectCreationCommand( WorkspaceId workspaceId, UUID correlationId) {
        this(UUID.randomUUID(), workspaceId, correlationId);
    }

    @Override
    public UUID aggregateId() {
        return workspaceId.value();
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
