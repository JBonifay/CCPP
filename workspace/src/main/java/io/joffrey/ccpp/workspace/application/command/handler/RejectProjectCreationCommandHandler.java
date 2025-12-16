package io.joffrey.ccpp.workspace.application.command.handler;

import com.ccpp.shared.infrastructure.command.CommandHandler;
import com.ccpp.shared.domain.event.WorkspaceProjectCreationRejected;
import com.ccpp.shared.infrastructure.event.EventBus;
import io.joffrey.ccpp.workspace.application.command.command.RejectProjectCreationCommand;

public class RejectProjectCreationCommandHandler implements CommandHandler<RejectProjectCreationCommand> {

    private final EventBus eventBus;

    public RejectProjectCreationCommandHandler(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    @Override
    public void handle(RejectProjectCreationCommand command) {
        WorkspaceProjectCreationRejected rejection = new WorkspaceProjectCreationRejected(
            command.workspaceId(),
            command.projectId(),
            command.reason()
        );
        eventBus.publish(rejection);
    }
}
