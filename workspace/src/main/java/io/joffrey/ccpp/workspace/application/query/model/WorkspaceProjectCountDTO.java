package io.joffrey.ccpp.workspace.application.query.model;

import com.ccpp.shared.identities.WorkspaceId;
import io.joffrey.ccpp.workspace.domain.model.Membership;

public record WorkspaceProjectCountDTO(
        WorkspaceId workspaceId,
        int projectCount,
        Membership membership
) {
}
