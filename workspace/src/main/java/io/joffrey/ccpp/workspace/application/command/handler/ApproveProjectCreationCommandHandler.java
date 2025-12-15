package io.joffrey.ccpp.workspace.application.command.handler;

import com.ccpp.shared.command.CommandHandler;
import com.ccpp.shared.domain.DomainEvent;
import com.ccpp.shared.domain.EventStore;
import io.joffrey.ccpp.workspace.application.command.command.ApproveProjectCreationCommand;
import io.joffrey.ccpp.workspace.domain.Workspace;

import java.util.List;

public class ApproveProjectCreationCommandHandler implements CommandHandler<ApproveProjectCreationCommand> {

    private final EventStore eventStore;

    public ApproveProjectCreationCommandHandler(EventStore eventStore) {
        this.eventStore = eventStore;
    }

    @Override
    public void handle(ApproveProjectCreationCommand approveProjectCreationCommand) {
        List<DomainEvent> workspaceEvents = eventStore.readStream(approveProjectCreationCommand.workspaceId().value());
        Workspace workspace = Workspace.fromHistory(workspaceEvents);

        workspace.approveProjectCreation();

        eventStore.append(approveProjectCreationCommand.workspaceId().value(), workspace.uncommittedEvents(), workspace.version());
        workspace.markEventsAsCommitted();
    }

}
