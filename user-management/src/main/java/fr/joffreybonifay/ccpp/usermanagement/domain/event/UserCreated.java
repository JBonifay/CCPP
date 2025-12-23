package fr.joffreybonifay.ccpp.usermanagement.domain.event;

import fr.joffreybonifay.ccpp.shared.domain.event.DomainEvent;
import fr.joffreybonifay.ccpp.shared.domain.identities.UserId;
import fr.joffreybonifay.ccpp.shared.domain.valueobjects.Email;

public record UserCreated(
        UserId userId,
        Email email,
        String passwordHash,
        String fullname
) implements DomainEvent {
}
