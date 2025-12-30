package fr.joffreybonifay.ccpp.usermanagement.infrastructure.projection;

import fr.joffreybonifay.ccpp.shared.domain.identities.UserId;
import fr.joffreybonifay.ccpp.shared.domain.identities.WorkspaceId;
import fr.joffreybonifay.ccpp.shared.domain.valueobjects.Email;
import fr.joffreybonifay.ccpp.usermanagement.application.query.model.UserDTO;
import fr.joffreybonifay.ccpp.usermanagement.application.query.model.WorkspaceDTO;
import fr.joffreybonifay.ccpp.usermanagement.application.query.repository.UserReadRepository;
import fr.joffreybonifay.ccpp.usermanagement.domain.event.UserAssignedToWorkspace;
import fr.joffreybonifay.ccpp.usermanagement.domain.event.UserCreated;
import fr.joffreybonifay.ccpp.usermanagement.infrastructure.query.InMemoryUserReadRepository;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class UserProjectionUpdaterTest {

    UserReadRepository repository = new InMemoryUserReadRepository();
    UserProjectionUpdater updater = new UserProjectionUpdater(repository);

    UserId userId = new UserId(UUID.randomUUID());
    WorkspaceId workspaceId = new WorkspaceId(UUID.randomUUID());
    String workspaceName = "Test workspace";
    String workspaceLogoUrl = "logo_url";

    @Test
    void should_create_projection_on_user_created() {
        UserCreated event = new UserCreated(
                userId,
                new Email("john@example.com"),
                "hashedPassword123",
                "John Doe"
        );

        updater.on(event);

        var projection = repository.findById(userId);
        assertThat(projection).isPresent();
        assertThat(projection.get()).isEqualTo(new UserDTO(
                userId,
                "john@example.com",
                "hashedPassword123",
                "John Doe",
                List.of()
        ));
    }

    @Test
    void should_find_user_by_email() {
        givenUserCreated();

        var projection = repository.findByEmail("john@example.com");

        assertThat(projection).isPresent();
        assertThat(projection.get().userId()).isEqualTo(userId);
    }

    @Test
    void should_add_workspace_on_user_assigned_to_workspace() {
        givenUserCreated();

        var event = new UserAssignedToWorkspace(userId, workspaceId, workspaceName, workspaceLogoUrl);

        updater.on(event);

        var projection = repository.findById(userId);
        assertThat(projection).isPresent();
        assertThat(projection.get().workspaces()).containsExactly(
                new WorkspaceDTO(workspaceId, workspaceName, workspaceLogoUrl)
        );
    }

    @Test
    void should_add_multiple_workspaces() {
        givenUserCreated();
        var workspaceId2 = new WorkspaceId(UUID.randomUUID());

        updater.on(new UserAssignedToWorkspace(userId, workspaceId, workspaceName, workspaceLogoUrl));
        updater.on(new UserAssignedToWorkspace(userId, workspaceId2, "otherName", "otherLogoUrl"));

        var projection = repository.findById(userId);
        assertThat(projection).isPresent();
        assertThat(projection.get().workspaces()).containsExactly(
                new WorkspaceDTO(workspaceId, workspaceName, workspaceLogoUrl),
                new WorkspaceDTO(workspaceId2, "otherName", "otherLogoUrl")
        );
    }

    private void givenUserCreated() {
        updater.on(new UserCreated(
                userId,
                new Email("john@example.com"),
                "hashedPassword123",
                "John Doe"
        ));
    }
}
