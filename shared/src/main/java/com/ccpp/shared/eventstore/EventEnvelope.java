package com.ccpp.shared.eventstore;

import java.time.Instant;
import java.util.UUID;

public record EventEnvelope<T>(
    UUID eventId,
    UUID aggregateId,
    String aggregateType,
    Long version,
    String eventType,
    T payload,
    Instant timestamp,
    UUID correlationId,
    UUID causationId
) {}
