package io.joffrey.ccpp.workspace.application.command.handler;

import com.ccpp.shared.command.CommandHandler;
import com.ccpp.shared.domain.DomainEvent;
import com.ccpp.shared.domain.EventStore;
import io.joffrey.ccpp.workspace.application.command.command.UpgradeWorkspaceSubscriptionCommand;
import io.joffrey.ccpp.workspace.domain.Workspace;

import java.util.List;

public class UpgradeWorkspaceSubscriptionCommandHandler implements CommandHandler<UpgradeWorkspaceSubscriptionCommand> {

    private final EventStore eventStore;

    public UpgradeWorkspaceSubscriptionCommandHandler(EventStore eventStore) {
        this.eventStore = eventStore;
    }

    @Override
    public void handle(UpgradeWorkspaceSubscriptionCommand command) {
        List<DomainEvent> workspaceEvents = eventStore.readStream(command.workspaceId().value());
        var workspace = Workspace.fromHistory(workspaceEvents);

        workspace.upgradeMembership();

        eventStore.append(command.workspaceId().value(), workspace.uncommittedEvents(), workspace.version());
        workspace.markEventsAsCommitted();
    }

}
