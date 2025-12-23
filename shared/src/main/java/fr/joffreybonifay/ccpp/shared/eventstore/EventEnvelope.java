package fr.joffreybonifay.ccpp.shared.eventstore;

import java.time.Instant;
import java.util.UUID;

public record EventEnvelope(
        UUID eventId,
        UUID aggregateId,
        AggregateType aggregateType,
        Long version,
        String eventType,
        UUID commandId,
        UUID correlationId,
        UUID causationId,
        Instant timestamp,
        String payload
) {
}
