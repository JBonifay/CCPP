package io.joffrey.ccpp.workspace.application.command.handler;

import com.ccpp.shared.identities.WorkspaceId;
import com.ccpp.shared.repository.InMemoryEventStore;
import io.joffrey.ccpp.workspace.application.command.command.ApproveProjectCreationCommand;
import io.joffrey.ccpp.workspace.domain.event.WorkspaceCreated;
import io.joffrey.ccpp.workspace.domain.event.WorkspaceProjectCreationApproved;
import io.joffrey.ccpp.workspace.domain.exception.ProjectLimitReachedException;
import io.joffrey.ccpp.workspace.domain.model.Membership;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class ApproveProjectCreationCommandHandlerTest {

    InMemoryEventStore eventStore = new InMemoryEventStore();
    ApproveProjectCreationCommandHandler handler = new ApproveProjectCreationCommandHandler(eventStore);

    WorkspaceId workspaceId = new WorkspaceId(UUID.randomUUID());

    @Test
    void should_approve_project_creation_freemium_0_projects() {
        eventStore.append(workspaceId.value(), List.of(new WorkspaceCreated(workspaceId, "Workspace name", Membership.FREEMIUM)), -1);

        handler.handle(new ApproveProjectCreationCommand(workspaceId));

        assertThat(eventStore.readStream(workspaceId.value()))
                .last()
                .isEqualTo(new WorkspaceProjectCreationApproved(workspaceId));
    }

    @Test
    void should_reject_project_creation_fremium_2_projects() {
        eventStore.append(workspaceId.value(), List.of(
                new WorkspaceCreated(workspaceId, "Workspace name", Membership.FREEMIUM),
                new WorkspaceProjectCreationApproved(workspaceId),
                new WorkspaceProjectCreationApproved(workspaceId)
        ), -1);

        assertThatThrownBy(() -> handler.handle(new ApproveProjectCreationCommand(workspaceId)))
                .isInstanceOf(ProjectLimitReachedException.class)
                .hasMessage("Workspace has already reached max projects limit for membership.");
    }

    @Test
    void should_create_infinite_project_premium() {
        eventStore.append(workspaceId.value(), List.of(
                new WorkspaceCreated(workspaceId, "Workspace name", Membership.PREMIUM),
                new WorkspaceProjectCreationApproved(workspaceId),
                new WorkspaceProjectCreationApproved(workspaceId),
                new WorkspaceProjectCreationApproved(workspaceId),
                new WorkspaceProjectCreationApproved(workspaceId),
                new WorkspaceProjectCreationApproved(workspaceId),
                new WorkspaceProjectCreationApproved(workspaceId),
                new WorkspaceProjectCreationApproved(workspaceId),
                new WorkspaceProjectCreationApproved(workspaceId),
                new WorkspaceProjectCreationApproved(workspaceId),
                new WorkspaceProjectCreationApproved(workspaceId)
        ), -1);

        handler.handle(new ApproveProjectCreationCommand(workspaceId));

        assertThat(eventStore.readStream(workspaceId.value()))
                .hasSize(12)
                .last()
                .isEqualTo(new WorkspaceProjectCreationApproved(workspaceId));
    }
}
