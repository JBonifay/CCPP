package fr.joffreybonifay.ccpp.workspace.application.command.handler;

import fr.joffreybonifay.ccpp.shared.event.WorkspaceProjectCreationApproved;
import fr.joffreybonifay.ccpp.shared.eventbus.EventBus;
import fr.joffreybonifay.ccpp.shared.eventbus.SimpleEventBus;
import fr.joffreybonifay.ccpp.shared.eventstore.InMemoryEventStore;
import fr.joffreybonifay.ccpp.shared.identities.ProjectId;
import fr.joffreybonifay.ccpp.shared.identities.WorkspaceId;
import fr.joffreybonifay.ccpp.workspace.application.command.command.ApproveProjectCreationCommand;
import fr.joffreybonifay.ccpp.workspace.domain.event.WorkspaceCreated;
import fr.joffreybonifay.ccpp.workspace.domain.event.WorkspaceProjectLimitReached;
import fr.joffreybonifay.ccpp.workspace.domain.exception.WorkspaceDoesNotExistException;
import fr.joffreybonifay.ccpp.workspace.domain.model.SubscriptionTier;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ApproveProjectCreationCommandHandlerTest {

    EventBus eventBus = new SimpleEventBus();
    InMemoryEventStore eventStore = new InMemoryEventStore(eventBus);
    ApproveProjectCreationCommandHandler approveProjectCreationCommandHandler = new ApproveProjectCreationCommandHandler(eventStore);

    ProjectId projectId = new ProjectId(UUID.randomUUID());
    WorkspaceId workspaceId = new WorkspaceId(UUID.randomUUID());

    UUID commandId = UUID.randomUUID();
    UUID correlationId = UUID.randomUUID();

    @Test
    void should_create_project() {
        eventStore.saveEvents(workspaceId.value(), List.of(
                new WorkspaceCreated(workspaceId, "Workspace name", SubscriptionTier.FREEMIUM)
        ), -1, null, null);

        approveProjectCreationCommandHandler.handle(new ApproveProjectCreationCommand(commandId, workspaceId, projectId, correlationId));

        assertThat(eventStore.loadEvents(workspaceId.value()))
                .last()
                .isEqualTo(new WorkspaceProjectCreationApproved(workspaceId, projectId));
    }

    @Test
    void should_fail_if_workspace_does_not_exists() {
        assertThatThrownBy(() -> approveProjectCreationCommandHandler.handle(new ApproveProjectCreationCommand(commandId, workspaceId, projectId, correlationId)))
                .isInstanceOf(WorkspaceDoesNotExistException.class)
                .hasMessage("Workspace does not exists");
    }

    @Test
    void should_inform_when_limit_reached() {
        eventStore.saveEvents(workspaceId.value(), List.of(
                new WorkspaceCreated(workspaceId, "Workspace name", SubscriptionTier.FREEMIUM),
                new WorkspaceProjectCreationApproved(workspaceId, new ProjectId(UUID.randomUUID()))
        ), -1, null, null);

        approveProjectCreationCommandHandler.handle(new ApproveProjectCreationCommand(commandId, workspaceId, projectId, correlationId));

        assertThat(eventStore.loadEvents(workspaceId.value()))
                .last()
                .isEqualTo(new WorkspaceProjectLimitReached(workspaceId));
    }

}
