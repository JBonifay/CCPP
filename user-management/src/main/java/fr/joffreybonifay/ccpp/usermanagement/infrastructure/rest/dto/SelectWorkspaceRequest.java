package fr.joffreybonifay.ccpp.usermanagement.infrastructure.rest.dto;

import java.util.UUID;

public record SelectWorkspaceRequest(
        String refreshToken,
        UUID workspaceId
) {
}
