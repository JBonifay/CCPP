package io.joffrey.ccpp.projectplanning.application.command.handler;

import com.ccpp.shared.infrastructure.event.DomainEvent;
import com.ccpp.shared.infrastructure.event.EventStore;
import com.ccpp.shared.infrastructure.command.CommandHandler;
import io.joffrey.ccpp.projectplanning.application.command.command.UpdateProjectDetailsCommand;
import io.joffrey.ccpp.projectplanning.domain.Project;

import java.util.List;

public class UpdateProjectDetailsHandler implements CommandHandler<UpdateProjectDetailsCommand> {

    private final EventStore eventStore;

    public UpdateProjectDetailsHandler(EventStore eventStore) {
        this.eventStore = eventStore;
    }

    @Override
    public void handle(UpdateProjectDetailsCommand command) {
        List<DomainEvent> projectEvents = eventStore.loadEvents(command.projectId().value());
        Project project = Project.fromHistory(projectEvents);

        project.updateDetails(command.title(), command.description());

        eventStore.saveEvents(command.projectId().value(), project.uncommittedEvents(), project.version());
        project.markEventsAsCommitted();

    }
}
