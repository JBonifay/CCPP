package fr.joffreybonifay.ccpp.workspace.infrastructure.rest.dto;

public record CreateWorkspaceRequest(
        String name,
        String logoUrl
) {
}
