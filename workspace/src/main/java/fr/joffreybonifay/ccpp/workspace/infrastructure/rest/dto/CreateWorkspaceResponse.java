package fr.joffreybonifay.ccpp.workspace.infrastructure.rest.dto;

import java.util.UUID;

public record CreateWorkspaceResponse(
        UUID workspaceId
) {
}
