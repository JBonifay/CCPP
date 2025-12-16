package com.ccpp.shared.infrastructure.event;

import java.util.List;
import java.util.UUID;

public interface EventStore {
    void saveEvents(UUID aggregateId, List<DomainEvent> events, int expectedVersion);
    List<DomainEvent> loadEvents(UUID aggregateId);
}
