package fr.joffreybonifay.ccpp.workspace.domain.event;

import fr.joffreybonifay.ccpp.shared.identities.WorkspaceId;
import fr.joffreybonifay.ccpp.shared.event.DomainEvent;
import fr.joffreybonifay.ccpp.workspace.domain.model.SubscriptionTier;

public record WorkspaceCreated(
        WorkspaceId workspaceId,
        String workspaceName,
        SubscriptionTier subscriptionTier
) implements DomainEvent {
}
