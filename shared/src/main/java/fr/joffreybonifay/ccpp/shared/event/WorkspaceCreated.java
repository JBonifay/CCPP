package fr.joffreybonifay.ccpp.shared.event;

import fr.joffreybonifay.ccpp.shared.identities.UserId;
import fr.joffreybonifay.ccpp.shared.identities.WorkspaceId;
import fr.joffreybonifay.ccpp.shared.model.SubscriptionTier;

public record WorkspaceCreated(
        WorkspaceId workspaceId,
        UserId userId,
        String workspaceName,
        SubscriptionTier subscriptionTier
) implements DomainEvent {
}
