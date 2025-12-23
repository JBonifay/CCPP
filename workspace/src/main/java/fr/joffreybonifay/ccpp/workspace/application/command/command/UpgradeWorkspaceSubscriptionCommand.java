package fr.joffreybonifay.ccpp.workspace.application.command.command;

import fr.joffreybonifay.ccpp.shared.command.Command;
import fr.joffreybonifay.ccpp.shared.identities.WorkspaceId;

import java.util.UUID;

public record UpgradeWorkspaceSubscriptionCommand(
        WorkspaceId workspaceId
)  implements Command {

    @Override
    public UUID commandId() {
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
