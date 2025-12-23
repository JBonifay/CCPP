package fr.joffreybonifay.ccpp.workspace.application.command.handler;

import fr.joffreybonifay.ccpp.shared.command.CommandHandler;
import fr.joffreybonifay.ccpp.shared.event.DomainEvent;
import fr.joffreybonifay.ccpp.shared.eventstore.AggregateType;
import fr.joffreybonifay.ccpp.shared.eventstore.EventMetadata;
import fr.joffreybonifay.ccpp.shared.eventstore.EventStore;
import fr.joffreybonifay.ccpp.workspace.application.command.command.ApproveProjectCreationCommand;
import fr.joffreybonifay.ccpp.workspace.domain.Workspace;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class ApproveProjectCreationCommandHandler implements CommandHandler<ApproveProjectCreationCommand> {

    private final EventStore eventStore;

    public ApproveProjectCreationCommandHandler(EventStore eventStore) {
        this.eventStore = eventStore;
    }

    @Override
    public void handle(ApproveProjectCreationCommand command) {
        log.info("Approve project creation - correlationId: {}", command.correlationId());

        List<DomainEvent> workspaceEvents = eventStore.loadEvents(command.workspaceId().value());
        Workspace workspace = Workspace.fromHistory(workspaceEvents);
        int initialVersion = workspace.version();

        workspace.approveProjectCreation(command.projectId());

        eventStore.saveEvents(
                command.workspaceId().value(),
                AggregateType.WORKSPACE,
                workspace.uncommittedEvents().stream().map(domainEvent -> new EventMetadata(
                        domainEvent,
                        command.commandId(),
                        command.correlationId(),
                        command.causationId()
                )).toList(),
                initialVersion
        );

        workspace.markEventsAsCommitted();
    }

}
