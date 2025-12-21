package fr.joffreybonifay.ccpp.usermanagement.domain.event;

import fr.joffreybonifay.ccpp.shared.event.DomainEvent;
import fr.joffreybonifay.ccpp.shared.identities.UserId;
import fr.joffreybonifay.ccpp.shared.valueobjects.Email;

public record UserCreated(
        UserId userId,
        Email email,
        String passwordHash,
        String fullname
) implements DomainEvent {
}
