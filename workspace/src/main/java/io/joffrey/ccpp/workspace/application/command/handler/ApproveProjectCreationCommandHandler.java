package io.joffrey.ccpp.workspace.application.command.handler;

import com.ccpp.shared.command.CommandHandler;
import com.ccpp.shared.domain.DomainEvent;
import com.ccpp.shared.domain.EventStore;
import com.ccpp.shared.event.EventPublisher;
import io.joffrey.ccpp.workspace.application.command.command.ApproveProjectCreationCommand;
import io.joffrey.ccpp.workspace.domain.Workspace;

import java.util.List;

public class ApproveProjectCreationCommandHandler implements CommandHandler<ApproveProjectCreationCommand> {

    private final EventStore eventStore;
    private final EventPublisher eventPublisher;

    public ApproveProjectCreationCommandHandler(EventStore eventStore, EventPublisher eventPublisher) {
        this.eventStore = eventStore;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public void handle(ApproveProjectCreationCommand approveProjectCreationCommand) {
        List<DomainEvent> workspaceEvents = eventStore.readStream(approveProjectCreationCommand.workspaceId().value());
        Workspace workspace = Workspace.fromHistory(workspaceEvents);

        workspace.approveProjectCreation();

        var events = workspace.uncommittedEvents();
        eventStore.append(approveProjectCreationCommand.workspaceId().value(), events, workspace.version());
        workspace.markEventsAsCommitted();

        events.forEach(eventPublisher::publish);
    }

}
