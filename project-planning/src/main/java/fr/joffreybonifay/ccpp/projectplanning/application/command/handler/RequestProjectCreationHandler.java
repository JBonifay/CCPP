package fr.joffreybonifay.ccpp.projectplanning.application.command.handler;

import fr.joffreybonifay.ccpp.shared.command.CommandHandler;
import fr.joffreybonifay.ccpp.shared.eventstore.AggregateType;
import fr.joffreybonifay.ccpp.shared.eventstore.EventMetadata;
import fr.joffreybonifay.ccpp.shared.eventstore.EventStore;
import fr.joffreybonifay.ccpp.projectplanning.application.command.command.RequestProjectCreationCommand;
import fr.joffreybonifay.ccpp.projectplanning.domain.Project;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RequestProjectCreationHandler implements CommandHandler<RequestProjectCreationCommand> {

    private final EventStore eventStore;

    public RequestProjectCreationHandler(EventStore eventStore) {
        this.eventStore = eventStore;
    }

    @Override
    public void handle(RequestProjectCreationCommand command) {
        log.info("Request new project creation - correlationId: {}", command.correlationId());
        var project = Project.create(
                command.workspaceId(),
                command.userId(),
                command.projectId(),
                command.title(),
                command.description(),
                command.timeline(),
                command.budgetLimit()
        );

        eventStore.saveEvents(
                command.projectId().value(),
                AggregateType.PROJECT_PLANNING,
                project.uncommittedEvents().stream().map(domainEvent -> new EventMetadata(
                        domainEvent,
                        command.commandId(),
                        command.correlationId(),
                        command.causationId()
                )).toList(),
                project.version()
        );

        project.markEventsAsCommitted();
    }

}
