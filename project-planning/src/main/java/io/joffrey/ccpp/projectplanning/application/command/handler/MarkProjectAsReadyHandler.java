package io.joffrey.ccpp.projectplanning.application.command.handler;

import com.ccpp.shared.domain.DomainEvent;
import com.ccpp.shared.domain.EventStore;
import io.joffrey.ccpp.projectplanning.application.command.command.MarkProjectAsReadyCommand;
import io.joffrey.ccpp.projectplanning.domain.Project;

import java.util.List;

public class MarkProjectAsReadyHandler implements com.ccpp.shared.command.CommandHandler<MarkProjectAsReadyCommand> {

    private final EventStore eventStore;

    public MarkProjectAsReadyHandler(EventStore eventStore) {
        this.eventStore = eventStore;
    }

    @Override
    public void handle(MarkProjectAsReadyCommand command) {
        List<DomainEvent> projectEvents = eventStore.readStream(command.projectId().value());
        Project project = Project.fromHistory(projectEvents);

        project.markAsReady(command.userId());
        eventStore.append(command.projectId().value(), project.uncommittedEvents(), project.version());
        project.markEventsAsCommitted();
    }
}
