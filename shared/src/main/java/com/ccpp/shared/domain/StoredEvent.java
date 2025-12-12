package com.ccpp.shared.domain;

import java.time.Instant;
import java.util.UUID;

public record StoredEvent(
    UUID eventId,
    UUID aggregateId,
    int version,
    Instant occurredOn,
    DomainEvent payload
) {}
