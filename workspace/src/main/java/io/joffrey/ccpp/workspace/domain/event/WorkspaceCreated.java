package io.joffrey.ccpp.workspace.domain.event;

import com.ccpp.shared.identities.WorkspaceId;
import io.joffrey.ccpp.workspace.domain.model.SubscriptionTier;

public record WorkspaceCreated(
        WorkspaceId workspaceId,
        String workspaceName,
        SubscriptionTier subscriptionTier
) implements WorkspaceDomainEvent {
}
