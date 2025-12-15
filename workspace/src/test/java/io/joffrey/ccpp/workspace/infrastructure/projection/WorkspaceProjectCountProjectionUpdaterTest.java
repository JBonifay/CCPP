package io.joffrey.ccpp.workspace.infrastructure.projection;

import com.ccpp.shared.identities.WorkspaceId;
import io.joffrey.ccpp.workspace.application.query.model.WorkspaceProjectCountDTO;
import io.joffrey.ccpp.workspace.application.query.repository.WorkspaceProjectCountReadRepository;
import io.joffrey.ccpp.workspace.domain.event.WorkspaceCreated;
import io.joffrey.ccpp.workspace.domain.event.WorkspaceProjectCreationApproved;
import io.joffrey.ccpp.workspace.domain.event.WorkspaceSubscriptionUpgraded;
import io.joffrey.ccpp.workspace.domain.model.SubscriptionTier;
import io.joffrey.ccpp.workspace.infrastructure.query.InMemoryWorkspaceProjectCountReadRepository;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class WorkspaceProjectCountProjectionUpdaterTest {

    WorkspaceProjectCountReadRepository repository = new InMemoryWorkspaceProjectCountReadRepository();
    WorkspaceProjectCountProjectionUpdater updater = new WorkspaceProjectCountProjectionUpdater(repository);

    WorkspaceId workspaceId = new WorkspaceId(UUID.randomUUID());

    @Test
    void should_create_projection_on_workspace_created() {
        var event = new WorkspaceCreated(workspaceId, "My Workspace", SubscriptionTier.FREEMIUM);

        updater.onEvent(event);

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

        updater.onEvent(new WorkspaceProjectCreationApproved(workspaceId));

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

        updater.onEvent(new WorkspaceSubscriptionUpgraded(workspaceId, SubscriptionTier.PREMIUM));

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
