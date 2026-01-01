package fr.joffreybonifay.ccpp.usermanagement.application.command;

import fr.joffreybonifay.ccpp.shared.command.Command;
import fr.joffreybonifay.ccpp.shared.domain.identities.UserId;
import fr.joffreybonifay.ccpp.shared.domain.identities.WorkspaceId;
import fr.joffreybonifay.ccpp.usermanagement.domain.model.UserRole;

import java.util.UUID;

public record AssignUserToWorkspaceCommand(
        UUID commandId,
        WorkspaceId workspaceId,
        String workspaceName,
        String workspaceLogoUrl,
        UserId userId,
        UserRole userRole,
        UUID correlationId,
        UUID causationId
) implements Command {

    public AssignUserToWorkspaceCommand(WorkspaceId workspaceId, String workspaceName, String workspaceLogoUrl, UserId userId, UserRole userRole, UUID correlationId, UUID causationId) {
        this(UUID.randomUUID(), workspaceId, workspaceName, workspaceLogoUrl, userId, userRole, correlationId, causationId);
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
