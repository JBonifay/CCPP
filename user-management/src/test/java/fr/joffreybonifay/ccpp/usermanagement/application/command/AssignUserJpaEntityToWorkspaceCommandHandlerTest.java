package fr.joffreybonifay.ccpp.usermanagement.application.command;

import fr.joffreybonifay.ccpp.shared.eventbus.EventBus;
import fr.joffreybonifay.ccpp.shared.eventbus.SimpleEventBus;
import fr.joffreybonifay.ccpp.shared.eventstore.EventStore;
import fr.joffreybonifay.ccpp.shared.eventstore.InMemoryEventStore;
import fr.joffreybonifay.ccpp.shared.identities.UserId;
import fr.joffreybonifay.ccpp.shared.identities.WorkspaceId;
import fr.joffreybonifay.ccpp.shared.valueobjects.Email;
import fr.joffreybonifay.ccpp.usermanagement.domain.event.UserAssignedToWorkspace;
import fr.joffreybonifay.ccpp.usermanagement.domain.event.UserCreated;
import fr.joffreybonifay.ccpp.usermanagement.domain.exception.UserDoesNotExistException;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AssignUserJpaEntityToWorkspaceCommandHandlerTest {

    EventBus eventBus = new SimpleEventBus();
    EventStore eventStore = new InMemoryEventStore(eventBus);
    AssignUserToWorkspaceCommandHandler handler = new AssignUserToWorkspaceCommandHandler(eventStore);

    UserId userId = new UserId(UUID.randomUUID());
    WorkspaceId workspaceId = new WorkspaceId(UUID.randomUUID());

    UUID commandId = UUID.randomUUID();
    UUID correlationId = UUID.randomUUID();

    @Test
    void should_assign_user_to_workspace() {
        eventStore.saveEvents(userId.value(), List.of(new UserCreated(userId, new Email("test@test.com"), "", "")), -1, null, null);

        handler.handle(new AssignUserToWorkspaceCommand(
                commandId,
                workspaceId,
                userId,
                correlationId,
                null
        ));

        assertThat(eventStore.loadEvents(userId.value()))
                .last()
                .isEqualTo(new UserAssignedToWorkspace(userId, workspaceId));

    }

    @Test
    void should_fail_if_user_does_not_exists() {
        assertThatThrownBy(() -> handler.handle(new AssignUserToWorkspaceCommand(
                commandId,
                workspaceId,
                new UserId(UUID.randomUUID()),
                correlationId,
                null
        )))
                .isInstanceOf(UserDoesNotExistException.class)
                .hasMessage("User does not exist");
    }
}
