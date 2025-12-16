package io.joffrey.ccpp.workspace.domain.event;

import com.ccpp.shared.domain.identities.WorkspaceId;
import com.ccpp.shared.infrastructure.event.DomainEvent;
import io.joffrey.ccpp.workspace.domain.model.SubscriptionTier;

public record WorkspaceSubscriptionUpgraded(
        WorkspaceId workspaceId,
        SubscriptionTier newSubscriptionTier
) implements DomainEvent {
}
