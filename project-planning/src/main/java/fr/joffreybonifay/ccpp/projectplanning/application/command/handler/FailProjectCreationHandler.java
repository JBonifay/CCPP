package fr.joffreybonifay.ccpp.projectplanning.application.command.handler;

import fr.joffreybonifay.ccpp.shared.command.CommandHandler;
import fr.joffreybonifay.ccpp.shared.event.DomainEvent;
import fr.joffreybonifay.ccpp.shared.eventstore.EventStore;
import fr.joffreybonifay.ccpp.projectplanning.application.command.command.FailProjectCreationCommand;
import fr.joffreybonifay.ccpp.projectplanning.domain.Project;

import java.util.List;

public class FailProjectCreationHandler implements CommandHandler<FailProjectCreationCommand> {

    private final EventStore eventStore;

    public FailProjectCreationHandler(EventStore eventStore) {
        this.eventStore = eventStore;
    }

    @Override
    public void handle(FailProjectCreationCommand command) {
        List<DomainEvent> projectEvents = eventStore.loadEvents(command.projectId().value());
        Project project = Project.fromHistory(projectEvents);
        int initialVersion = project.version();

        project.failCreation(command.reason());

        eventStore.saveEvents(
            command.projectId().value(),
            project.uncommittedCHanges(),
            initialVersion,
            command.correlationId(),
            command.causationId()
        );

        project.markEventsAsCommitted();
    }
}
