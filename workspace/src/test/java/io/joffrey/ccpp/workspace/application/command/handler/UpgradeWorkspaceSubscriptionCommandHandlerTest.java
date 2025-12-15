package io.joffrey.ccpp.workspace.application.command.handler;

import com.ccpp.shared.identities.WorkspaceId;
import com.ccpp.shared.repository.InMemoryEventStore;
import io.joffrey.ccpp.workspace.application.command.command.UpgradeWorkspaceSubscriptionCommand;
import io.joffrey.ccpp.workspace.domain.event.WorkspaceCreated;
import io.joffrey.ccpp.workspace.domain.event.WorkspaceSubscriptionUpgraded;
import io.joffrey.ccpp.workspace.domain.exception.MembershipException;
import io.joffrey.ccpp.workspace.domain.model.Membership;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class UpgradeWorkspaceSubscriptionCommandHandlerTest {

    InMemoryEventStore eventStore = new InMemoryEventStore();
    UpgradeWorkspaceSubscriptionCommandHandler handler = new UpgradeWorkspaceSubscriptionCommandHandler(eventStore);

    @Test
    void should_upgrade_subscribtion() {
        var workspaceId = new WorkspaceId(UUID.randomUUID());
        eventStore.append(workspaceId.value(), List.of(new WorkspaceCreated(workspaceId, "WorkspaceName", Membership.FREEMIUM)), -1);

        handler.handle(new UpgradeWorkspaceSubscriptionCommand(workspaceId));

        assertThat(eventStore.readStream(workspaceId.value()))
                .last()
                .isEqualTo(new WorkspaceSubscriptionUpgraded(workspaceId, Membership.PREMIUM));
    }

    @Test
    void should_fail_if_workspace_is_already_premium() {
        var workspaceId = new WorkspaceId(UUID.randomUUID());
        eventStore.append(workspaceId.value(), List.of(
                new WorkspaceCreated(workspaceId, "WorkspaceName", Membership.FREEMIUM),
                new WorkspaceSubscriptionUpgraded(workspaceId, Membership.PREMIUM)
        ), -1);

        assertThatThrownBy(() -> handler.handle(new UpgradeWorkspaceSubscriptionCommand(workspaceId)))
                .isInstanceOf(MembershipException.class)
                .hasMessage("Already premium member");
    }
}
