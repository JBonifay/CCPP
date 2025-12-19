package io.joffrey.ccpp.workspace.domain.event;

import com.ccpp.shared.identities.WorkspaceId;
import com.ccpp.shared.event.DomainEvent;
import io.joffrey.ccpp.workspace.domain.model.SubscriptionTier;

public record WorkspaceCreated(
        WorkspaceId workspaceId,
        String workspaceName,
        SubscriptionTier subscriptionTier
) implements DomainEvent {
}
