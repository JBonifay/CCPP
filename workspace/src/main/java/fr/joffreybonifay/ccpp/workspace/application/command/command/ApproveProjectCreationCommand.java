package fr.joffreybonifay.ccpp.workspace.application.command.command;


import fr.joffreybonifay.ccpp.shared.command.Command;
import fr.joffreybonifay.ccpp.shared.domain.identities.ProjectId;
import fr.joffreybonifay.ccpp.shared.domain.identities.WorkspaceId;

import java.util.UUID;

public record ApproveProjectCreationCommand(
        UUID commandId,
        WorkspaceId workspaceId,
        ProjectId projectId,
        UUID correlationId,
        UUID causationId
) implements Command {

    public ApproveProjectCreationCommand(WorkspaceId workspaceId, ProjectId projectId, UUID correlationId, UUID causationId) {
        this(UUID.randomUUID(), workspaceId, projectId, correlationId, causationId);
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
