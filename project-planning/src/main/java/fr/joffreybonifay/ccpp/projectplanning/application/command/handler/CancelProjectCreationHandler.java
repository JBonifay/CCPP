package fr.joffreybonifay.ccpp.projectplanning.application.command.handler;

import fr.joffreybonifay.ccpp.shared.command.CommandHandler;
import fr.joffreybonifay.ccpp.shared.event.DomainEvent;
import fr.joffreybonifay.ccpp.shared.eventstore.EventStore;
import fr.joffreybonifay.ccpp.projectplanning.application.command.command.CancelProjectCreationCommand;
import fr.joffreybonifay.ccpp.projectplanning.domain.Project;

import java.util.List;

public class CancelProjectCreationHandler implements CommandHandler<CancelProjectCreationCommand> {

    private final EventStore eventStore;

    public CancelProjectCreationHandler(EventStore eventStore) {
        this.eventStore = eventStore;
    }

    @Override
    public void handle(CancelProjectCreationCommand command) {
        List<DomainEvent> projectEvents = eventStore.loadEvents(command.projectId().value());
        Project project = Project.fromHistory(projectEvents);
        int initialVersion = project.version();

        project.cancel(command.reason());

        eventStore.saveEvents(command.projectId().value(), project.uncommittedEvents(), initialVersion, command.correlationId(), command.causationId());
        project.markEventsAsCommitted();
    }

}
