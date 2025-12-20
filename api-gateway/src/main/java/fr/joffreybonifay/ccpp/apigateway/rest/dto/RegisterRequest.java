package fr.joffreybonifay.ccpp.apigateway.rest.dto;

import java.util.UUID;

public record RegisterRequest(
        String email,
        String password,
        UUID workspaceId
) {
}
