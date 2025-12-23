package fr.joffreybonifay.ccpp.shared.eventstore;

import fr.joffreybonifay.ccpp.shared.domain.event.DomainEvent;

import java.time.Instant;
import java.util.UUID;

public record EventMetadata(
        UUID eventId,
        DomainEvent domainEvent,
        Instant occurredOn,
        UUID commandId,
        UUID correlationId,
        UUID causationId
) {

    public EventMetadata(DomainEvent domainEvent, UUID commandId, UUID correlationId, UUID causationId) {
        this(UUID.randomUUID(), domainEvent, Instant.now(), commandId, correlationId, causationId);
    }
}
