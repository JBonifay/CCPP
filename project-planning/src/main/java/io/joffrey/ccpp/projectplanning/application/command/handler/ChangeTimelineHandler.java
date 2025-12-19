package io.joffrey.ccpp.projectplanning.application.command.handler;

import com.ccpp.shared.event.DomainEvent;
import com.ccpp.shared.eventstore.EventStore;
import com.ccpp.shared.command.CommandHandler;
import io.joffrey.ccpp.projectplanning.application.command.command.ChangeTimelineCommand;
import io.joffrey.ccpp.projectplanning.domain.Project;

import java.util.List;

public class ChangeTimelineHandler implements CommandHandler<ChangeTimelineCommand> {

    private final EventStore eventStore;

    public ChangeTimelineHandler(EventStore eventStore) {
        this.eventStore = eventStore;
    }

    @Override
    public void handle(ChangeTimelineCommand command) {
        List<DomainEvent> projectEvents = eventStore.loadEvents(command.projectId().value());
        Project project = Project.fromHistory(projectEvents);
        int initialVersion = project.version();

        project.changeTimeline(command.newTimeline());

        eventStore.saveEvents(command.projectId().value(), project.uncommittedCHanges(), initialVersion, command.correlationId(), command.causationId());
        project.markEventsAsCommitted();
    }

}
