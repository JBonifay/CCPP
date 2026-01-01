package fr.joffreybonifay.ccpp.usermanagement.application.query.model;

import fr.joffreybonifay.ccpp.shared.domain.identities.WorkspaceId;
import fr.joffreybonifay.ccpp.usermanagement.domain.model.UserRole;

public record WorkspaceDTO(
        WorkspaceId workspaceId,
        String workspaceName,
        String workspaceLogoUrl,
        UserRole userRole
) {
}
