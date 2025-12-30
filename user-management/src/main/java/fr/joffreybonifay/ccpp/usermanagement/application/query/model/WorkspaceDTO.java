package fr.joffreybonifay.ccpp.usermanagement.application.query.model;

import fr.joffreybonifay.ccpp.shared.domain.identities.WorkspaceId;

public record WorkspaceDTO(
        WorkspaceId workspaceId,
        String workspaceName,
        String workspaceLogoUrl
) {
}
