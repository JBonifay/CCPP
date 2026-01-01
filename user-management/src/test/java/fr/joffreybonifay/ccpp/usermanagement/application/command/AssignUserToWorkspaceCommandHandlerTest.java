package fr.joffreybonifay.ccpp.usermanagement.application.command;

import fr.joffreybonifay.ccpp.shared.domain.identities.UserId;
import fr.joffreybonifay.ccpp.shared.domain.identities.WorkspaceId;
import fr.joffreybonifay.ccpp.shared.domain.valueobjects.Email;
import fr.joffreybonifay.ccpp.shared.eventstore.AggregateType;
import fr.joffreybonifay.ccpp.shared.eventstore.EventMetadata;
import fr.joffreybonifay.ccpp.shared.eventstore.EventStore;
import fr.joffreybonifay.ccpp.shared.eventstore.impl.InMemoryEventStore;
import fr.joffreybonifay.ccpp.usermanagement.domain.event.UserAssignedToWorkspace;
import fr.joffreybonifay.ccpp.usermanagement.domain.event.UserCreated;
import fr.joffreybonifay.ccpp.usermanagement.domain.exception.UserDoesNotExistException;
import fr.joffreybonifay.ccpp.usermanagement.domain.model.UserRole;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AssignUserToWorkspaceCommandHandlerTest {

    EventStore eventStore = new InMemoryEventStore();
    AssignUserToWorkspaceCommandHandler handler = new AssignUserToWorkspaceCommandHandler(eventStore);

    UserId userId = new UserId(UUID.randomUUID());
    WorkspaceId workspaceId = new WorkspaceId(UUID.randomUUID());
    String workspaceName = "Test workspace";
    String workspaceLogoUrl = "logo_url";

    UUID commandId = UUID.randomUUID();
    UUID correlationId = UUID.randomUUID();

    @Test
    void should_assign_user_to_workspace() {
        eventStore.saveEvents(
                userId.value(),
                AggregateType.USER,
                List.of(new EventMetadata(new UserCreated(userId, new Email("test@test.com"), "", ""), null, null, null)),
                -1
        );

        handler.handle(new AssignUserToWorkspaceCommand(
                commandId,
                workspaceId,
                workspaceName,
                workspaceLogoUrl,
                userId,
                UserRole.ADMIN,
                correlationId,
                null
        ));

        assertThat(eventStore.loadEvents(userId.value()))
                .last()
                .isEqualTo(new UserAssignedToWorkspace(userId, UserRole.ADMIN, workspaceId, workspaceName, workspaceLogoUrl));

    }

    @Test
    void should_fail_if_user_does_not_exists() {
        assertThatThrownBy(() -> handler.handle(new AssignUserToWorkspaceCommand(
                commandId,
                workspaceId,
                workspaceName,
                workspaceLogoUrl,
                new UserId(UUID.randomUUID()),
                UserRole.USER,
                correlationId,
                null
        )))
                .isInstanceOf(UserDoesNotExistException.class)
                .hasMessage("User does not exist");
    }
}
