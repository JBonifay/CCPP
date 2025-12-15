package io.joffrey.ccpp.workspace.application.saga;

import com.ccpp.shared.command.CommandBus;
import com.ccpp.shared.domain.DomainEvent;
import com.ccpp.shared.domain.EventStore;
import com.ccpp.shared.domain.event.ProjectCreated;
import io.joffrey.ccpp.workspace.application.command.command.ApproveProjectCreationCommand;
import io.joffrey.ccpp.workspace.application.command.command.RejectProjectCreationCommand;
import io.joffrey.ccpp.workspace.domain.Workspace;
import io.joffrey.ccpp.workspace.domain.exception.ProjectLimitReachedException;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class WorkspaceProjectCreationSagaHandler {

    private final EventStore eventStore;
    private final CommandBus commandBus;

    public WorkspaceProjectCreationSagaHandler(EventStore eventStore, CommandBus commandBus) {
        this.eventStore = eventStore;
        this.commandBus = commandBus;
    }

    @EventListener
    public void onProjectCreated(ProjectCreated event) {
        List<DomainEvent> workspaceEvents = eventStore.readStream(event.workspaceId().value());
        Workspace workspace = Workspace.fromHistory(workspaceEvents);

        try {
            workspace.approveProjectCreation();
            commandBus.execute(new ApproveProjectCreationCommand(event.workspaceId()));
        } catch (ProjectLimitReachedException e) {
            commandBus.execute(new RejectProjectCreationCommand(
                    event.workspaceId(),
                    event.projectId(),
                    e.getMessage()
            ));
        }
    }
}

