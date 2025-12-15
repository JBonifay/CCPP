package io.joffrey.ccpp.workspace.application.command.command;

import com.ccpp.shared.command.Command;
import com.ccpp.shared.identities.ProjectId;
import com.ccpp.shared.identities.WorkspaceId;

public record RejectProjectCreationCommand(
        WorkspaceId workspaceId,
        ProjectId projectId,
        String reason
) implements Command {
}
