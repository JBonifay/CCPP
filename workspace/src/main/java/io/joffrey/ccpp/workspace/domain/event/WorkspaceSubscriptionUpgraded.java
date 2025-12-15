package io.joffrey.ccpp.workspace.domain.event;

import com.ccpp.shared.identities.WorkspaceId;
import io.joffrey.ccpp.workspace.domain.model.SubscriptionTier;

public record WorkspaceSubscriptionUpgraded(
        WorkspaceId workspaceId,
        SubscriptionTier newSubscriptionTier
) implements WorkspaceDomainEvent {
}
