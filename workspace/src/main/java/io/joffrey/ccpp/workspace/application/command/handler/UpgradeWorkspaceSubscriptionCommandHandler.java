package io.joffrey.ccpp.workspace.application.command.handler;

import com.ccpp.shared.infrastructure.command.CommandHandler;
import com.ccpp.shared.infrastructure.event.DomainEvent;
import com.ccpp.shared.infrastructure.event.EventStore;
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
        List<DomainEvent> workspaceEvents = eventStore.loadEvents(command.workspaceId().value());
        var workspace = Workspace.fromHistory(workspaceEvents);

        workspace.upgradeSubscriptionTier();

        eventStore.saveEvents(command.workspaceId().value(), workspace.uncommittedEvents(), workspace.version());
        workspace.markEventsAsCommitted();
    }

}
