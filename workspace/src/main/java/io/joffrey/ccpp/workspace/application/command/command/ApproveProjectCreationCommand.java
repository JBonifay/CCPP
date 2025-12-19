package io.joffrey.ccpp.workspace.application.command.command;


import com.ccpp.shared.command.Command;
import com.ccpp.shared.identities.WorkspaceId;

import java.util.UUID;

public record ApproveProjectCreationCommand(
        WorkspaceId workspaceId
) implements Command {
    @Override
    public UUID getCommandId() {
        return null;
    }

    @Override
    public UUID getAggregateId() {
        return null;
    }

    @Override
    public UUID getCorrelationId() {
        return null;
    }

    @Override
    public UUID getCausationId() {
        return null;
    }
}
