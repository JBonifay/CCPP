package io.joffrey.ccpp.workspace.domain.event;

import com.ccpp.shared.domain.identities.WorkspaceId;
import com.ccpp.shared.infrastructure.event.DomainEvent;
import io.joffrey.ccpp.workspace.domain.model.SubscriptionTier;

public record WorkspaceCreated(
        WorkspaceId workspaceId,
        String workspaceName,
        SubscriptionTier subscriptionTier
) implements DomainEvent {
}
