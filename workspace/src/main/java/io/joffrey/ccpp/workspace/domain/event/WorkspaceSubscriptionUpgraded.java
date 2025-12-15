package io.joffrey.ccpp.workspace.domain.event;

import com.ccpp.shared.identities.WorkspaceId;
import io.joffrey.ccpp.workspace.domain.model.Membership;

public record WorkspaceSubscriptionUpgraded(
        WorkspaceId workspaceId,
        Membership newMembership
) implements WorkspaceDomainEvent {
}
