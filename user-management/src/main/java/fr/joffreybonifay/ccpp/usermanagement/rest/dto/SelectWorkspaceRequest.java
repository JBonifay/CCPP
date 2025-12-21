package fr.joffreybonifay.ccpp.usermanagement.rest.dto;

import java.util.UUID;

public record SelectWorkspaceRequest(
        String refreshToken,
        UUID workspaceId
) {
}
