package io.joffrey.ccpp.workspace.application.command.handler;

import com.ccpp.shared.identities.WorkspaceId;
import com.ccpp.shared.eventstore.InMemoryEventStore;
import io.joffrey.ccpp.workspace.application.command.command.CreateWorkspaceCommand;
import io.joffrey.ccpp.workspace.domain.event.WorkspaceCreated;
import io.joffrey.ccpp.workspace.domain.exception.InvalidWorkspaceDataException;
import io.joffrey.ccpp.workspace.domain.model.SubscriptionTier;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CreateWorkspaceCommandHandlerTest {

    InMemoryEventStore eventStore = new InMemoryEventStore();
    CreateWorkspaceCommandHandler createWorkspaceCommandHandler = new CreateWorkspaceCommandHandler(eventStore);

    @Test
    void should_create_workspace() {
        var workspaceId = new WorkspaceId(UUID.randomUUID());

        createWorkspaceCommandHandler.handle(new CreateWorkspaceCommand(workspaceId, "Creator workspace"));

        assertThat(eventStore.loadEvents(workspaceId.value()))
                .last()
                .isEqualTo(new WorkspaceCreated(workspaceId, "Creator workspace", SubscriptionTier.FREEMIUM));
    }

    @Test
    void should_fail_if_workspaceId_null() {
        assertThatThrownBy(() -> createWorkspaceCommandHandler.handle(new CreateWorkspaceCommand(new WorkspaceId(null), "FOOBAR")))
                .isInstanceOf(InvalidWorkspaceDataException.class)
                .hasMessage("Workspace id cannot be empty");

        assertThatThrownBy(() -> createWorkspaceCommandHandler.handle(new CreateWorkspaceCommand(null, "FOOBAR")))
                .isInstanceOf(InvalidWorkspaceDataException.class)
                .hasMessage("Workspace id cannot be empty");
    }

    @Test
    void should_fail_if_workspace_name_empty_or_null() {
        assertThatThrownBy(() -> createWorkspaceCommandHandler.handle(new CreateWorkspaceCommand(new WorkspaceId(UUID.randomUUID()), "")))
                .isInstanceOf(InvalidWorkspaceDataException.class)
                .hasMessage("Workspace name cannot be empty");

        assertThatThrownBy(() -> createWorkspaceCommandHandler.handle(new CreateWorkspaceCommand(new WorkspaceId(UUID.randomUUID()), null)))
                .isInstanceOf(InvalidWorkspaceDataException.class)
                .hasMessage("Workspace name cannot be empty");
    }

}
