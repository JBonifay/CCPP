package fr.joffreybonifay.ccpp.workspace.application.command.handler;

import fr.joffreybonifay.ccpp.shared.command.CommandHandler;
import fr.joffreybonifay.ccpp.shared.event.DomainEvent;
import fr.joffreybonifay.ccpp.shared.eventstore.EventStore;
import fr.joffreybonifay.ccpp.workspace.application.command.command.ApproveProjectCreationCommand;
import fr.joffreybonifay.ccpp.workspace.domain.Workspace;

import java.util.List;

public class ApproveProjectCreationCommandHandler implements CommandHandler<ApproveProjectCreationCommand> {

    private final EventStore eventStore;

    public ApproveProjectCreationCommandHandler(EventStore eventStore) {
        this.eventStore = eventStore;
    }

    @Override
    public void handle(ApproveProjectCreationCommand command) {
        List<DomainEvent> workspaceEvents = eventStore.loadEvents(command.workspaceId().value());
        Workspace workspace = Workspace.fromHistory(workspaceEvents);
        int initialVersion = workspace.version();

        workspace.approveProjectCreation();

        eventStore.saveEvents(command.aggregateId(), workspace.uncommittedCHanges(), initialVersion, command.correlationId(), command.causationId());
        workspace.markEventsAsCommitted();
    }

}
