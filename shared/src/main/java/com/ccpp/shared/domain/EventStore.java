package com.ccpp.shared.domain;

import java.util.List;

public interface EventStore {
    void append(String streamId, List<DomainEvent> events, int expectedVersion);
    List<DomainEvent> readStream(String streamId);
    List<DomainEvent> readAllEvents();
}
