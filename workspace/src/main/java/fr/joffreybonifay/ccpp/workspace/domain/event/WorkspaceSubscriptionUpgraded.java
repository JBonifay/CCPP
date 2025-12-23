package fr.joffreybonifay.ccpp.workspace.domain.event;

import fr.joffreybonifay.ccpp.shared.domain.identities.WorkspaceId;
import fr.joffreybonifay.ccpp.shared.domain.event.DomainEvent;
import fr.joffreybonifay.ccpp.shared.domain.model.SubscriptionTier;

public record WorkspaceSubscriptionUpgraded(
        WorkspaceId workspaceId,
        SubscriptionTier newSubscriptionTier
) implements DomainEvent {
}
