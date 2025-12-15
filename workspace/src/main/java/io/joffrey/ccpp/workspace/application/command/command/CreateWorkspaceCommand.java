package io.joffrey.ccpp.workspace.application.command.command;

import com.ccpp.shared.command.Command;
import com.ccpp.shared.identities.WorkspaceId;

public record CreateWorkspaceCommand(
        WorkspaceId workspaceId,
        String workspaceName
) implements Command {
}
