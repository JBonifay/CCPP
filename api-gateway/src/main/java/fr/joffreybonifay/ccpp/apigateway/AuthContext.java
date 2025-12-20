package fr.joffreybonifay.ccpp.apigateway;

import java.util.UUID;

public record AuthContext(
        UUID userId,
        UUID workspaceId
) {
}
