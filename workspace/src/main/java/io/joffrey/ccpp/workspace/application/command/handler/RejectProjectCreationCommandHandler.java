package io.joffrey.ccpp.workspace.application.command.handler;

import com.ccpp.shared.command.CommandHandler;
import com.ccpp.shared.event.EventPublisher;
import io.joffrey.ccpp.workspace.application.command.command.RejectProjectCreationCommand;
import io.joffrey.ccpp.workspace.domain.event.WorkspaceProjectCreationRejected;

public class RejectProjectCreationCommandHandler implements CommandHandler<RejectProjectCreationCommand> {

    private final EventPublisher eventPublisher;

    public RejectProjectCreationCommandHandler(EventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    @Override
    public void handle(RejectProjectCreationCommand command) {
        // Publish rejection event to other bounded contexts (e.g., project-planning saga)
        WorkspaceProjectCreationRejected rejection = new WorkspaceProjectCreationRejected(
            command.workspaceId(),
            command.projectId(),
            command.reason()
        );
        eventPublisher.publish(rejection);
    }
}