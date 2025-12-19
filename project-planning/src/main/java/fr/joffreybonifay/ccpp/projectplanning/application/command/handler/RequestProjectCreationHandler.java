package fr.joffreybonifay.ccpp.projectplanning.application.command.handler;

import fr.joffreybonifay.ccpp.shared.command.CommandHandler;
import fr.joffreybonifay.ccpp.shared.eventstore.EventStore;
import fr.joffreybonifay.ccpp.projectplanning.application.command.command.RequestProjectCreationCommand;
import fr.joffreybonifay.ccpp.projectplanning.domain.Project;

public class RequestProjectCreationHandler implements CommandHandler<RequestProjectCreationCommand> {

    private final EventStore eventStore;

    public RequestProjectCreationHandler(EventStore eventStore) {
        this.eventStore = eventStore;
    }

    @Override
    public void handle(RequestProjectCreationCommand command) {
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
                project.aggregateId(),
                project.uncommittedCHanges(),
                project.version(),
                command.correlationId(),
                command.commandId()
        );

        project.markEventsAsCommitted();
    }

}
