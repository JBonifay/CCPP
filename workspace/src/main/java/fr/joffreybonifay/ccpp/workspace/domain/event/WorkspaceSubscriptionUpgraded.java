package fr.joffreybonifay.ccpp.workspace.domain.event;

import fr.joffreybonifay.ccpp.shared.identities.WorkspaceId;
import fr.joffreybonifay.ccpp.shared.event.DomainEvent;
import fr.joffreybonifay.ccpp.workspace.domain.model.SubscriptionTier;

public record WorkspaceSubscriptionUpgraded(
        WorkspaceId workspaceId,
        SubscriptionTier newSubscriptionTier
) implements DomainEvent {
}
