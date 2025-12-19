package io.joffrey.ccpp.projectplanning.application.command.handler;

import com.ccpp.shared.event.DomainEvent;
import com.ccpp.shared.eventstore.EventStore;
import com.ccpp.shared.command.CommandHandler;
import io.joffrey.ccpp.projectplanning.application.command.command.MarkProjectAsReadyCommand;
import io.joffrey.ccpp.projectplanning.domain.Project;

import java.util.List;

public class MarkProjectAsReadyHandler implements CommandHandler<MarkProjectAsReadyCommand> {

    private final EventStore eventStore;

    public MarkProjectAsReadyHandler(EventStore eventStore) {
        this.eventStore = eventStore;
    }

    @Override
    public void handle(MarkProjectAsReadyCommand command) {
//        List<DomainEvent> projectEvents = eventStore.loadEvents(command.projectId().value());
//        Project project = Project.fromHistory(projectEvents);
//
//        project.markAsReady(command.userId());
//        eventStore.saveEvents(command.projectId().value(), project.uncommittedEvents(), project.version());
//        project.markEventsAsCommitted();
    }
}
