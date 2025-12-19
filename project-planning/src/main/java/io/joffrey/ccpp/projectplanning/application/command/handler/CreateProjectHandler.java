package io.joffrey.ccpp.projectplanning.application.command.handler;

import com.ccpp.shared.command.CommandHandler;
import com.ccpp.shared.eventstore.EventStore;
import io.joffrey.ccpp.projectplanning.application.command.command.CreateProjectCommand;

public class CreateProjectHandler implements CommandHandler<CreateProjectCommand> {

    private final EventStore eventStore;

    public CreateProjectHandler(EventStore eventStore) {
        this.eventStore = eventStore;
    }

    @Override
    public void handle(CreateProjectCommand command) {
//        var project = Project.create(
//                command.workspaceId(),
//                command.userId(),
//                command.projectId(),
//                command.title(),
//                command.description(),
//                command.timeline(),
//                command.budgetLimit()
//        );
//
//        var events = project.uncommittedEvents();
//        eventStore.saveEvents(command.projectId().value(), events, project.version());
//        project.markEventsAsCommitted();
//
//        events.forEach(eventBus::publish);
//
//        eventBus.publish(new ProjectCreationRequested(command.projectId(), command.workspaceId(), command.userId(), command.title(), command.budgetLimit()));
    }

}
