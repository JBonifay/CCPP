package fr.joffreybonifay.ccpp.shared.event;

import fr.joffreybonifay.ccpp.shared.identities.UserId;
import fr.joffreybonifay.ccpp.shared.valueobjects.Email;

public record UserRegisteredEvent(
        UserId userId,
        Email email,
        String displayName
) {
}
