package fr.joffreybonifay.ccpp.workspace.application.command.command;

import fr.joffreybonifay.ccpp.shared.command.Command;
import fr.joffreybonifay.ccpp.shared.domain.identities.UserId;
import fr.joffreybonifay.ccpp.shared.domain.identities.WorkspaceId;

import java.util.UUID;

public record CreateWorkspaceCommand(
        UUID commandId,
        WorkspaceId workspaceId,
        UserId userId,
        String workspaceName,
        String logoUrl,
        UUID correlationId,
        UUID causationId
) implements Command {

    public CreateWorkspaceCommand(WorkspaceId workspaceId, UserId userId, String workspaceName, String logoUrl) {
        this(UUID.randomUUID(), workspaceId, userId, workspaceName, logoUrl, null, null);
    }

    @Override
    public UUID commandId() {
        return commandId;
    }

    @Override
    public UUID correlationId() {
        return correlationId != null ? correlationId : commandId;
    }

    @Override
    public UUID causationId() {
        return causationId != null ? causationId : commandId;
    }
}
