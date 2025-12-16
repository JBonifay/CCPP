package io.joffrey.ccpp.workspace.application.command.handler;

import com.ccpp.shared.command.CommandHandler;
import com.ccpp.shared.domain.event.WorkspaceProjectCreationRejected;
import com.ccpp.shared.event.EventPublisher;
import io.joffrey.ccpp.workspace.application.command.command.RejectProjectCreationCommand;

public class RejectProjectCreationCommandHandler implements CommandHandler<RejectProjectCreationCommand> {

    private final EventPublisher eventPublisher;

    public RejectProjectCreationCommandHandler(EventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    @Override
    public void handle(RejectProjectCreationCommand command) {
        WorkspaceProjectCreationRejected rejection = new WorkspaceProjectCreationRejected(
            command.workspaceId(),
            command.projectId(),
            command.reason()
        );
        eventPublisher.publish(rejection);
    }
}
