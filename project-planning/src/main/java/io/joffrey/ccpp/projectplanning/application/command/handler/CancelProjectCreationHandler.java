package io.joffrey.ccpp.projectplanning.application.command.handler;

import com.ccpp.shared.command.CommandHandler;
import com.ccpp.shared.event.DomainEvent;
import com.ccpp.shared.eventstore.EventStore;
import io.joffrey.ccpp.projectplanning.application.command.command.CancelProjectCreationCommand;
import io.joffrey.ccpp.projectplanning.domain.Project;

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

        eventStore.saveEvents(command.projectId().value(), project.uncommittedCHanges(), initialVersion, command.correlationId(), command.causationId());
        project.markEventsAsCommitted();
    }

}
