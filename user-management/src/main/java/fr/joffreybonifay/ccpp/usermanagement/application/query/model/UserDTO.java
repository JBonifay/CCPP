package fr.joffreybonifay.ccpp.usermanagement.application.query.model;

import fr.joffreybonifay.ccpp.shared.domain.identities.UserId;

import java.util.Set;
import java.util.UUID;

public record UserDTO(
        UserId userId,
        String email,
        String passwordHash,
        String fullName,
        Set<UUID> workspaceIds
) {
}
