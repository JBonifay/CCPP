package io.joffrey.ccpp.workspace.application.command.handler;

import com.ccpp.shared.command.CommandHandler;
import com.ccpp.shared.domain.EventStore;
import io.joffrey.ccpp.workspace.application.command.command.CreateWorkspaceCommand;
import io.joffrey.ccpp.workspace.domain.Workspace;

public class CreateWorkspaceCommandHandler implements CommandHandler<CreateWorkspaceCommand> {

    private final EventStore eventStore;

    public CreateWorkspaceCommandHandler(EventStore eventStore) {
        this.eventStore = eventStore;
    }

    @Override
    public void handle(CreateWorkspaceCommand createWorkspaceCommand) {
        Workspace workspace = Workspace.create(
                createWorkspaceCommand.workspaceId(),
                createWorkspaceCommand.workspaceName()
        );

        eventStore.append(createWorkspaceCommand.workspaceId().value(), workspace.uncommittedEvents(), workspace.version());
        workspace.markEventsAsCommitted();
    }

}
