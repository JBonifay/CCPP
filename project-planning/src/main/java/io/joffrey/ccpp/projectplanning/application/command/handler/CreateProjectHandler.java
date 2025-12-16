package io.joffrey.ccpp.projectplanning.application.command.handler;

import com.ccpp.shared.domain.EventStore;
import com.ccpp.shared.event.EventPublisher;
import io.joffrey.ccpp.projectplanning.application.command.command.CreateProjectCommand;
import io.joffrey.ccpp.projectplanning.domain.Project;

public class CreateProjectHandler implements com.ccpp.shared.command.CommandHandler<CreateProjectCommand> {

    private final EventStore eventStore;
    private final EventPublisher eventPublisher;

    public CreateProjectHandler(EventStore eventStore, EventPublisher eventPublisher) {
        this.eventStore = eventStore;
        this.eventPublisher = eventPublisher;
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
        eventStore.append(command.projectId().value(), events, project.version());
        project.markEventsAsCommitted();

        events.forEach(eventPublisher::publish);
    }
}
