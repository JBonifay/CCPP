package fr.joffreybonifay.ccpp.projectplanning.application.command.handler;

import fr.joffreybonifay.ccpp.shared.command.CommandHandler;
import fr.joffreybonifay.ccpp.shared.event.DomainEvent;
import fr.joffreybonifay.ccpp.shared.eventstore.EventStore;
import fr.joffreybonifay.ccpp.projectplanning.application.command.command.UpdateDetailsCommand;
import fr.joffreybonifay.ccpp.projectplanning.domain.Project;

import java.util.List;

public class UpdateDetailsHandler implements CommandHandler<UpdateDetailsCommand> {

    private final EventStore eventStore;

    public UpdateDetailsHandler(EventStore eventStore) {
        this.eventStore = eventStore;
    }

    @Override
    public void handle(UpdateDetailsCommand command) {
        List<DomainEvent> projectEvents = eventStore.loadEvents(command.projectId().value());
        Project project = Project.fromHistory(projectEvents);
        int initialVersion = project.version();

        project.updateDetails(command.title(), command.description());

        eventStore.saveEvents(command.projectId().value(), project.uncommittedCHanges(), initialVersion, command.correlationId(), command.causationId());
        project.markEventsAsCommitted();
    }
}
