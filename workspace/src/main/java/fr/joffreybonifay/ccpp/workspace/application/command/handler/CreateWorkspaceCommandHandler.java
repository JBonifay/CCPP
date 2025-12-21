package fr.joffreybonifay.ccpp.workspace.application.command.handler;

import fr.joffreybonifay.ccpp.shared.command.CommandHandler;
import fr.joffreybonifay.ccpp.shared.eventstore.EventStore;
import fr.joffreybonifay.ccpp.workspace.application.command.command.CreateWorkspaceCommand;
import fr.joffreybonifay.ccpp.workspace.domain.Workspace;

public class CreateWorkspaceCommandHandler implements CommandHandler<CreateWorkspaceCommand> {

    private final EventStore eventStore;

    public CreateWorkspaceCommandHandler(EventStore eventStore) {
        this.eventStore = eventStore;
    }

    @Override
    public void handle(CreateWorkspaceCommand createWorkspaceCommand) {
        Workspace workspace = Workspace.create(
                createWorkspaceCommand.workspaceId(),
                createWorkspaceCommand.userId(),
                createWorkspaceCommand.workspaceName()
        );

        eventStore.saveEvents(
                createWorkspaceCommand.workspaceId().value(),
                workspace.uncommittedEvents(),
                workspace.version(),
                createWorkspaceCommand.correlationId(),
                createWorkspaceCommand.causationId()
        );
        workspace.markEventsAsCommitted();
    }

}
