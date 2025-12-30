package fr.joffreybonifay.ccpp.shared.domain.event;

import fr.joffreybonifay.ccpp.shared.domain.identities.UserId;
import fr.joffreybonifay.ccpp.shared.domain.identities.WorkspaceId;
import fr.joffreybonifay.ccpp.shared.domain.model.SubscriptionTier;

public record WorkspaceCreated(
        WorkspaceId workspaceId,
        UserId userId,
        String workspaceName,
        String logoUrl,
        SubscriptionTier subscriptionTier
) implements DomainEvent {
}
