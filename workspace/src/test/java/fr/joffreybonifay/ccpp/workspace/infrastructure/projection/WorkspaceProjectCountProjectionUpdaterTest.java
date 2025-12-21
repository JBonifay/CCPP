package fr.joffreybonifay.ccpp.workspace.infrastructure.projection;

import fr.joffreybonifay.ccpp.shared.event.WorkspaceProjectCreationApproved;
import fr.joffreybonifay.ccpp.shared.identities.ProjectId;
import fr.joffreybonifay.ccpp.shared.identities.UserId;
import fr.joffreybonifay.ccpp.shared.identities.WorkspaceId;
import fr.joffreybonifay.ccpp.workspace.application.query.model.WorkspaceProjectCountDTO;
import fr.joffreybonifay.ccpp.workspace.application.query.repository.WorkspaceProjectCountReadRepository;
import fr.joffreybonifay.ccpp.shared.event.WorkspaceCreated;
import fr.joffreybonifay.ccpp.workspace.domain.event.WorkspaceSubscriptionUpgraded;
import fr.joffreybonifay.ccpp.shared.model.SubscriptionTier;
import fr.joffreybonifay.ccpp.workspace.infrastructure.query.InMemoryWorkspaceProjectCountReadRepository;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class WorkspaceProjectCountProjectionUpdaterTest {

    WorkspaceProjectCountReadRepository repository = new InMemoryWorkspaceProjectCountReadRepository();
    WorkspaceProjectCountProjectionUpdater updater = new WorkspaceProjectCountProjectionUpdater(repository);

    WorkspaceId workspaceId = new WorkspaceId(UUID.randomUUID());
    UserId userId = new UserId(UUID.randomUUID());

    @Test
    void should_create_projection_on_workspace_created() {
        var event = new WorkspaceCreated(workspaceId, userId, "My Workspace", SubscriptionTier.FREEMIUM);

        updater.handle(event);

        var projection = repository.findById(workspaceId);
        assertThat(projection.get()).isEqualTo(
                new WorkspaceProjectCountDTO(
                        workspaceId,
                        0,
                        SubscriptionTier.FREEMIUM
                ));
    }

    @Test
    void should_increment_project_count_on_approval() {
        repository.save(new WorkspaceProjectCountDTO(workspaceId, 0, SubscriptionTier.FREEMIUM));

        updater.handle(new WorkspaceProjectCreationApproved(workspaceId, new ProjectId(UUID.randomUUID())));

        var projection = repository.findById(workspaceId);
        assertThat(projection.get()).isEqualTo(
                new WorkspaceProjectCountDTO(
                        workspaceId,
                        1,
                        SubscriptionTier.FREEMIUM
                )
        );
    }

    @Test
    void should_update_tier_on_subscription_upgrade() {
        repository.save(new WorkspaceProjectCountDTO(workspaceId, 2, SubscriptionTier.FREEMIUM));

        updater.handle(new WorkspaceSubscriptionUpgraded(workspaceId, SubscriptionTier.PREMIUM));

        var projection = repository.findById(workspaceId);
        assertThat(projection.get()).isEqualTo(
                new WorkspaceProjectCountDTO(
                        workspaceId,
                        2,
                        SubscriptionTier.PREMIUM
                )
        );
    }

}
