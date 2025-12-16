package io.joffrey.ccpp.projectplanning.application.command.handler;

import com.ccpp.shared.infrastructure.event.EventStore;
import com.ccpp.shared.domain.event.ProjectCreationRequested;
import com.ccpp.shared.infrastructure.event.EventBus;
import com.ccpp.shared.infrastructure.command.CommandHandler;
import io.joffrey.ccpp.projectplanning.application.command.command.CreateProjectCommand;
import io.joffrey.ccpp.projectplanning.domain.Project;

public class CreateProjectHandler implements CommandHandler<CreateProjectCommand> {

    private final EventStore eventStore;
    private final EventBus eventBus;

    public CreateProjectHandler(EventStore eventStore, EventBus eventBus) {
        this.eventStore = eventStore;
        this.eventBus = eventBus;
    }

    @Override
    public void handle(CreateProjectCommand command) {
        var project = Project.create(
                command.workspaceId(),
                command.userId(),
                command.projectId(),
                command.title(),
                command.description(),
                command.timeline(),
                command.budgetLimit()
        );

        var events = project.uncommittedEvents();
        eventStore.saveEvents(command.projectId().value(), events, project.version());
        project.markEventsAsCommitted();

        events.forEach(eventBus::publish);

        eventBus.publish(new ProjectCreationRequested(command.projectId(), command.workspaceId(), command.userId(), command.title(), command.budgetLimit()));
    }

}
