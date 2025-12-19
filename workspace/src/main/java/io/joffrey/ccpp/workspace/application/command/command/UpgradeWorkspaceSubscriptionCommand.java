package io.joffrey.ccpp.workspace.application.command.command;

import com.ccpp.shared.command.Command;
import com.ccpp.shared.identities.WorkspaceId;

import java.util.UUID;

public record UpgradeWorkspaceSubscriptionCommand(
        WorkspaceId workspaceId
)  implements Command {
    @Override
    public UUID commandId() {
        return null;
    }

    @Override
    public UUID aggregateId() {
        return null;
    }

    @Override
    public UUID correlationId() {
        return null;
    }

    @Override
    public UUID causationId() {
        return null;
    }
}
