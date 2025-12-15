package io.joffrey.ccpp.projectplanning.application.saga;

import com.ccpp.shared.command.CommandBus;
import com.ccpp.shared.domain.event.WorkspaceProjectCreationRejected;
import io.joffrey.ccpp.projectplanning.application.command.command.CancelProjectCreationCommand;

public class ProjectCreationRejectionSagaHandler {

    private final CommandBus commandBus;

    public ProjectCreationRejectionSagaHandler(CommandBus commandBus) {
        this.commandBus = commandBus;
    }

    public void onProjectCreationRejected(WorkspaceProjectCreationRejected rejection) {
        commandBus.execute(new CancelProjectCreationCommand(rejection.projectId(), rejection.reason()));
    }
}
