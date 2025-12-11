package io.joffrey.ccpp.projectplanning.application.command.handler;

import com.ccpp.shared.domain.EventStore;
import io.joffrey.ccpp.projectplanning.application.command.ChangeProjectTimelineCommand;
import io.joffrey.ccpp.projectplanning.domain.Project;

public class ChangeProjectTimelineHandler implements CommandHandler<ChangeProjectTimelineCommand> {

    private final EventStore eventStore;

    public ChangeProjectTimelineHandler(EventStore eventStore) {
        this.eventStore = eventStore;
    }

    @Override
    public void handle(ChangeProjectTimelineCommand command) {
        var streamId = command.projectId().value();
        var events = eventStore.readStream(streamId);
        var project = new Project(command.projectId());
        project.loadFromHistory(events);

        project.changeTimeline(command.newTimeline());

        var newEvents = project.uncommittedEvents();
        int expectedVersion = events.size() - 1;
        eventStore.append(streamId, newEvents, expectedVersion);
        project.markEventsAsCommitted();
    }
}
