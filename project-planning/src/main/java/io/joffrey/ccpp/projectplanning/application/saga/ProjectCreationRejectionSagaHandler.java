package io.joffrey.ccpp.projectplanning.application.saga;

import com.ccpp.shared.command.CommandBus;
import com.ccpp.shared.domain.event.WorkspaceProjectCreationRejected;
import io.joffrey.ccpp.projectplanning.application.command.command.CancelProjectCreationCommand;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class ProjectCreationRejectionSagaHandler {

    private final CommandBus commandBus;

    public ProjectCreationRejectionSagaHandler(CommandBus commandBus) {
        this.commandBus = commandBus;
    }

    @EventListener
    public void onProjectCreationRejected(WorkspaceProjectCreationRejected rejection) {
        commandBus.execute(new CancelProjectCreationCommand(rejection.projectId(), rejection.reason()));
    }
}
