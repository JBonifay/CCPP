package fr.joffreybonifay.ccpp.workspace.infrastructure.rest.dto;

public record CreateWorkspaceResponse(
        String workspaceId,
        String workspaceName,
        String workspaceLogoUrl
) {
}
