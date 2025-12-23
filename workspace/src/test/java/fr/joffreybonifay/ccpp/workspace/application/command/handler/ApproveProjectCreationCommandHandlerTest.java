package fr.joffreybonifay.ccpp.workspace.application.command.handler;

import fr.joffreybonifay.ccpp.shared.domain.event.WorkspaceCreated;
import fr.joffreybonifay.ccpp.shared.domain.event.WorkspaceProjectCreationApproved;
import fr.joffreybonifay.ccpp.shared.eventstore.AggregateType;
import fr.joffreybonifay.ccpp.shared.eventstore.EventMetadata;
import fr.joffreybonifay.ccpp.shared.eventstore.impl.InMemoryEventStore;
import fr.joffreybonifay.ccpp.shared.domain.identities.ProjectId;
import fr.joffreybonifay.ccpp.shared.domain.identities.UserId;
import fr.joffreybonifay.ccpp.shared.domain.identities.WorkspaceId;
import fr.joffreybonifay.ccpp.shared.domain.model.SubscriptionTier;
import fr.joffreybonifay.ccpp.workspace.application.command.command.ApproveProjectCreationCommand;
import fr.joffreybonifay.ccpp.workspace.domain.event.WorkspaceProjectLimitReached;
import fr.joffreybonifay.ccpp.workspace.domain.exception.WorkspaceDoesNotExistException;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ApproveProjectCreationCommandHandlerTest {

    InMemoryEventStore eventStore = new InMemoryEventStore();
    ApproveProjectCreationCommandHandler approveProjectCreationCommandHandler = new ApproveProjectCreationCommandHandler(eventStore);

    WorkspaceId workspaceId = new WorkspaceId(UUID.randomUUID());
    UserId userId = new UserId(UUID.randomUUID());
    ProjectId projectId = new ProjectId(UUID.randomUUID());

    UUID commandId = UUID.randomUUID();
    UUID correlationId = UUID.randomUUID();

    @Test
    void should_create_project() {
        eventStore.saveEvents(
                workspaceId.value(),
                AggregateType.WORKSPACE,
                List.of(new EventMetadata(new WorkspaceCreated(workspaceId, userId, "Workspace name", SubscriptionTier.FREEMIUM), null, null, null)),
                -1)
        ;

        approveProjectCreationCommandHandler.handle(new ApproveProjectCreationCommand(commandId, workspaceId, projectId, correlationId, commandId));

        assertThat(eventStore.loadEvents(workspaceId.value()))
                .last()
                .isEqualTo(new WorkspaceProjectCreationApproved(workspaceId, projectId));
    }

    @Test
    void should_fail_if_workspace_does_not_exists() {
        assertThatThrownBy(() -> approveProjectCreationCommandHandler.handle(new ApproveProjectCreationCommand(commandId, workspaceId, projectId, correlationId, commandId)))
                .isInstanceOf(WorkspaceDoesNotExistException.class)
                .hasMessage("Workspace does not exists");
    }

    @Test
    void should_inform_when_limit_reached() {
        eventStore.saveEvents(
                workspaceId.value(),
                AggregateType.WORKSPACE,
                List.of(new EventMetadata(new WorkspaceCreated(workspaceId, userId, "Workspace name", SubscriptionTier.FREEMIUM), null, null, null),
                        new EventMetadata(new WorkspaceProjectCreationApproved(workspaceId, new ProjectId(UUID.randomUUID())), null, null, null)),
                -1);

        approveProjectCreationCommandHandler.handle(new ApproveProjectCreationCommand(commandId, workspaceId, projectId, correlationId, commandId));

        assertThat(eventStore.loadEvents(workspaceId.value()))
                .last()
                .isEqualTo(new WorkspaceProjectLimitReached(workspaceId));
    }

}
