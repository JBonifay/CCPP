package io.joffrey.ccpp.workspace.application.command.command;

import com.ccpp.shared.infrastructure.command.Command;
import com.ccpp.shared.domain.identities.WorkspaceId;

public record CreateWorkspaceCommand(
        WorkspaceId workspaceId,
        String workspaceName
) implements Command {
}
