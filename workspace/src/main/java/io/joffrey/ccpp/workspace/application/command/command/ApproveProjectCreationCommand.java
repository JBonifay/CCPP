package io.joffrey.ccpp.workspace.application.command.command;

import com.ccpp.shared.identities.WorkspaceId;

public record ApproveProjectCreationCommand(
        WorkspaceId workspaceId
) {
}
