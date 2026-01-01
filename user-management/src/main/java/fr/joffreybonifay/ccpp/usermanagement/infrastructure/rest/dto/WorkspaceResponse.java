package fr.joffreybonifay.ccpp.usermanagement.infrastructure.rest.dto;

import fr.joffreybonifay.ccpp.usermanagement.domain.model.UserRole;

public record WorkspaceResponse(
        String workspaceId,
        String workspaceName,
        String workspaceLogoUrl,
        UserRole userRole
) {
}
