package com.ccpp.shared.domain;

import java.util.List;
import java.util.UUID;

public interface EventStore {
    void append(UUID streamId, List<DomainEvent> events, int expectedVersion);
    List<DomainEvent> readStream(UUID streamId);
}
