package fr.joffreybonifay.ccpp.shared.eventstore;

import java.time.Instant;
import java.util.UUID;

public record EventEnvelope(
        UUID eventId,
        UUID aggregateId,
        String aggregateType,
        Long version,
        String eventType,
        UUID correlationId,
        UUID causationId,
        Instant timestamp,
        String payload
) {
}
