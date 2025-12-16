package io.joffrey.ccpp.workspace.application.command.command;

import com.ccpp.shared.infrastructure.command.Command;
import com.ccpp.shared.domain.identities.WorkspaceId;

public record UpgradeWorkspaceSubscriptionCommand(
        WorkspaceId workspaceId
)  implements Command {
}
