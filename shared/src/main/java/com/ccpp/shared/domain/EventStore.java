package com.ccpp.shared.domain;

import java.util.List;
import java.util.UUID;

public interface EventStore {
    void append(UUID aggregateId, List<DomainEvent> events, int expectedVersion);
    List<DomainEvent> readStream(UUID aggregateId);
    void subscribe(EventListener listener);
}
