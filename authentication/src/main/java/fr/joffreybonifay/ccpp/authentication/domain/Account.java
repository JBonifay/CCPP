package fr.joffreybonifay.ccpp.authentication.domain;

import fr.joffreybonifay.ccpp.shared.identities.WorkspaceId;
import fr.joffreybonifay.ccpp.shared.valueobjects.Email;

import java.util.UUID;

public record Account(
        UUID userId,
        Email email,
        WorkspaceId workspaceId,
        String displayName,
        String passwordHash
) {

    public static Account create(Email email, WorkspaceId workspaceId, String displayName, String passwordHash) {
        return new Account(UUID.randomUUID(), email, workspaceId, displayName, passwordHash);
    }

}
