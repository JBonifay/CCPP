package fr.joffreybonifay.ccpp.usermanagement.infrastructure.rest.dto;

public record WorkspaceResponse(
        String workspaceId,
        String workspaceName,
        String workspaceLogoUrl
) {
}
