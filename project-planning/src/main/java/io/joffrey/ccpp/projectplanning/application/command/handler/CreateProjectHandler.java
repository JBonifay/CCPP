package io.joffrey.ccpp.projectplanning.application.command.handler;

import com.ccpp.shared.domain.EventStore;
import io.joffrey.ccpp.projectplanning.application.command.command.CreateProjectCommand;
import io.joffrey.ccpp.projectplanning.domain.Project;

public class CreateProjectHandler implements com.ccpp.shared.command.CommandHandler<CreateProjectCommand> {

    private final EventStore eventStore;

    public CreateProjectHandler(EventStore eventStore) {
        this.eventStore = eventStore;
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

        eventStore.append(command.projectId().value(), project.uncommittedEvents(), project.version());
        project.markEventsAsCommitted();
    }
}
