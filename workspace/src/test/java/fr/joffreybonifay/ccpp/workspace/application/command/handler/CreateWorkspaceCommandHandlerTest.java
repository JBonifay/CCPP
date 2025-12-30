package fr.joffreybonifay.ccpp.workspace.application.command.handler;

import fr.joffreybonifay.ccpp.shared.domain.event.WorkspaceCreated;
import fr.joffreybonifay.ccpp.shared.domain.identities.UserId;
import fr.joffreybonifay.ccpp.shared.domain.identities.WorkspaceId;
import fr.joffreybonifay.ccpp.shared.domain.model.SubscriptionTier;
import fr.joffreybonifay.ccpp.shared.eventstore.impl.InMemoryEventStore;
import fr.joffreybonifay.ccpp.workspace.application.command.command.CreateWorkspaceCommand;
import fr.joffreybonifay.ccpp.workspace.domain.exception.InvalidWorkspaceDataException;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CreateWorkspaceCommandHandlerTest {

    InMemoryEventStore eventStore = new InMemoryEventStore();
    CreateWorkspaceCommandHandler createWorkspaceCommandHandler = new CreateWorkspaceCommandHandler(eventStore);

    UserId userId = new UserId(UUID.randomUUID());
    WorkspaceId workspaceId = new WorkspaceId(UUID.randomUUID());

    @Test
    void should_create_workspace() {
        createWorkspaceCommandHandler.handle(new CreateWorkspaceCommand(workspaceId, userId, "Creator workspace", "logo_url"));

        assertThat(eventStore.loadEvents(workspaceId.value()))
                .last()
                .isEqualTo(new WorkspaceCreated(workspaceId, userId, "Creator workspace", "logo_url", SubscriptionTier.FREEMIUM));
    }

    @Test
    void should_fail_if_workspaceId_null() {
        assertThatThrownBy(() -> createWorkspaceCommandHandler.handle(new CreateWorkspaceCommand(new WorkspaceId(null), userId, "FOOBAR", "logo_url")))
                .isInstanceOf(InvalidWorkspaceDataException.class)
                .hasMessage("Workspace id cannot be empty");

        assertThatThrownBy(() -> createWorkspaceCommandHandler.handle(new CreateWorkspaceCommand(null, userId, "FOOBAR", "logo_url")))
                .isInstanceOf(InvalidWorkspaceDataException.class)
                .hasMessage("Workspace id cannot be empty");
    }

    @Test
    void should_fail_if_workspace_name_empty_or_null() {
        assertThatThrownBy(() -> createWorkspaceCommandHandler.handle(new CreateWorkspaceCommand(new WorkspaceId(UUID.randomUUID()), userId, "", "logo_url")))
                .isInstanceOf(InvalidWorkspaceDataException.class)
                .hasMessage("Workspace name cannot be empty");

        assertThatThrownBy(() -> createWorkspaceCommandHandler.handle(new CreateWorkspaceCommand(new WorkspaceId(UUID.randomUUID()), userId, null, "logo_url")))
                .isInstanceOf(InvalidWorkspaceDataException.class)
                .hasMessage("Workspace name cannot be empty");
    }

    @Test
    void should_fail_if_user_id_missing() {
        assertThatThrownBy(() -> createWorkspaceCommandHandler.handle(new CreateWorkspaceCommand(new WorkspaceId(UUID.randomUUID()), null, "FOOBAR", "logo_url")))
                .isInstanceOf(InvalidWorkspaceDataException.class)
                .hasMessage("User id cannot be empty");

        assertThatThrownBy(() -> createWorkspaceCommandHandler.handle(new CreateWorkspaceCommand(new WorkspaceId(UUID.randomUUID()), new UserId(null), "FOOBAR", "logo_url")))
                .isInstanceOf(InvalidWorkspaceDataException.class)
                .hasMessage("User id cannot be empty");
    }

}
