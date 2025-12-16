package io.joffrey.ccpp.projectplanning.application.command.handler;

import com.ccpp.shared.infrastructure.event.DomainEvent;
import com.ccpp.shared.infrastructure.event.EventStore;
import com.ccpp.shared.infrastructure.command.CommandHandler;
import io.joffrey.ccpp.projectplanning.application.command.command.ChangeProjectTimelineCommand;
import io.joffrey.ccpp.projectplanning.domain.Project;

import java.util.List;

public class ChangeProjectTimelineHandler implements CommandHandler<ChangeProjectTimelineCommand> {

    private final EventStore eventStore;

    public ChangeProjectTimelineHandler(EventStore eventStore) {
        this.eventStore = eventStore;
    }

    @Override
    public void handle(ChangeProjectTimelineCommand command) {
        List<DomainEvent> projectEvents = eventStore.loadEvents(command.projectId().value());
        Project project = Project.fromHistory(projectEvents);

        project.changeTimeline(command.newTimeline());
        eventStore.saveEvents(command.projectId().value(), project.uncommittedEvents(), project.version());
        project.markEventsAsCommitted();
    }

}
