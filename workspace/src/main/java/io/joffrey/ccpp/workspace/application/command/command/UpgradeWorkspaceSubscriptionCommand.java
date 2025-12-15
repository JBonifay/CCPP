package io.joffrey.ccpp.workspace.application.command.command;

import com.ccpp.shared.command.Command;
import com.ccpp.shared.identities.WorkspaceId;

public record UpgradeWorkspaceSubscriptionCommand(
        WorkspaceId workspaceId
)  implements Command {
}
