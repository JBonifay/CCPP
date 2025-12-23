package fr.joffreybonifay.ccpp.workspace.application.query.model;

import fr.joffreybonifay.ccpp.shared.domain.identities.WorkspaceId;
import fr.joffreybonifay.ccpp.shared.domain.model.SubscriptionTier;

public record WorkspaceProjectCountDTO(
        WorkspaceId workspaceId,
        int projectCount,
        SubscriptionTier subscriptionTier
) {
}
