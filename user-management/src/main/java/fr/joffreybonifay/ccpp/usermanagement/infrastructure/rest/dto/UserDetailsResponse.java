package fr.joffreybonifay.ccpp.usermanagement.infrastructure.rest.dto;

import fr.joffreybonifay.ccpp.shared.domain.identities.UserId;

public record UserDetailsResponse(
        String id,
        String email,
        String name,
        String role
) {
    public UserDetailsResponse(UserId userId, String email, String name, String user) {
        this(userId.value().toString(), email, name, user);
    }
}
