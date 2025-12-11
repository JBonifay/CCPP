package io.joffrey.ccpp.projectplanning.application.command.handler;

import com.ccpp.shared.domain.EventStore;
import io.joffrey.ccpp.projectplanning.application.command.CreateProjectCommand;
import io.joffrey.ccpp.projectplanning.domain.Project;

public class CreateProjectHandler implements CommandHandler<CreateProjectCommand> {

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

        var events = project.uncommittedEvents();
        eventStore.append(command.projectId().value(), events, -1);
        project.clearUncommittedEvents();
    }
}
