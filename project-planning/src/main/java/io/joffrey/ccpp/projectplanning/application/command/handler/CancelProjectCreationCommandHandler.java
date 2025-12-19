package io.joffrey.ccpp.projectplanning.application.command.handler;

import com.ccpp.shared.command.CommandHandler;
import com.ccpp.shared.event.DomainEvent;
import com.ccpp.shared.eventstore.EventStore;
import io.joffrey.ccpp.projectplanning.application.command.command.CancelProjectCreationCommand;
import io.joffrey.ccpp.projectplanning.domain.Project;

import java.util.List;

public class CancelProjectCreationCommandHandler implements CommandHandler<CancelProjectCreationCommand> {

    private final EventStore eventStore;

    public CancelProjectCreationCommandHandler(EventStore eventStore) {
        this.eventStore = eventStore;
    }

    @Override
    public void handle(CancelProjectCreationCommand cancelProjectCreationCommand) {
//        List<DomainEvent> projectEvents = eventStore.loadEvents(cancelProjectCreationCommand.projectId().value());
//        Project project = Project.fromHistory(projectEvents);
//
//        project.cancel(cancelProjectCreationCommand.reason());
//
//        eventStore.saveEvents(cancelProjectCreationCommand.projectId().value(), project.uncommittedEvents(), project.version());
//        project.markEventsAsCommitted();
    }

}
