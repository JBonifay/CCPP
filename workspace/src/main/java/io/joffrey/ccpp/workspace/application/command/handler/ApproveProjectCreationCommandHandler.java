package io.joffrey.ccpp.workspace.application.command.handler;

import com.ccpp.shared.infrastructure.command.CommandHandler;
import com.ccpp.shared.infrastructure.event.DomainEvent;
import com.ccpp.shared.infrastructure.event.EventStore;
import com.ccpp.shared.infrastructure.event.EventBus;
import io.joffrey.ccpp.workspace.application.command.command.ApproveProjectCreationCommand;
import io.joffrey.ccpp.workspace.domain.Workspace;

import java.util.List;

public class ApproveProjectCreationCommandHandler implements CommandHandler<ApproveProjectCreationCommand> {

    private final EventStore eventStore;
    private final EventBus eventBus;

    public ApproveProjectCreationCommandHandler(EventStore eventStore, EventBus eventBus) {
        this.eventStore = eventStore;
        this.eventBus = eventBus;
    }

    @Override
    public void handle(ApproveProjectCreationCommand approveProjectCreationCommand) {
        List<DomainEvent> workspaceEvents = eventStore.loadEvents(approveProjectCreationCommand.workspaceId().value());
        Workspace workspace = Workspace.fromHistory(workspaceEvents);

        workspace.approveProjectCreation();

        var events = workspace.uncommittedEvents();
        eventStore.saveEvents(approveProjectCreationCommand.workspaceId().value(), events, workspace.version());
        workspace.markEventsAsCommitted();

        events.forEach(eventBus::publish);
    }

}
