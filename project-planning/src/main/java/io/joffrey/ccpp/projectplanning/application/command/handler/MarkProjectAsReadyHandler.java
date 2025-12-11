package io.joffrey.ccpp.projectplanning.application.command.handler;

import com.ccpp.shared.domain.EventStore;
import io.joffrey.ccpp.projectplanning.application.command.MarkProjectAsReadyCommand;
import io.joffrey.ccpp.projectplanning.domain.Project;

public class MarkProjectAsReadyHandler implements CommandHandler<MarkProjectAsReadyCommand> {

    private final EventStore eventStore;

    public MarkProjectAsReadyHandler(EventStore eventStore) {
        this.eventStore = eventStore;
    }

    @Override
    public void handle(MarkProjectAsReadyCommand command) {
        var streamId = command.projectId().value();
        var events = eventStore.readStream(streamId);
        var project = new Project(command.projectId());
        project.loadFromHistory(events);

        project.markAsReady(command.userId());

        var newEvents = project.uncommittedEvents();
        int expectedVersion = events.size() - 1;
        eventStore.append(streamId, newEvents, expectedVersion);
        project.markEventsAsCommitted();
    }
}
