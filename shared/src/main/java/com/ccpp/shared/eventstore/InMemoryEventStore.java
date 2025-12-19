package com.ccpp.shared.eventstore;

import com.ccpp.shared.event.DomainEvent;
import com.ccpp.shared.exception.OptimisticLockException;

import java.util.*;

public class InMemoryEventStore implements EventStore {

    private final Map<UUID, List<DomainEvent>> events = new HashMap<>();

    @Override
    public void saveEvents(UUID aggregateId, List<DomainEvent> events, int expectedVersion, UUID correlationId, UUID causationId) {
        List<DomainEvent> stream = this.events.computeIfAbsent(aggregateId, id -> new ArrayList<>());

        int currentVersion = (stream.size() - 1);

        if (currentVersion != expectedVersion) {
            throw new OptimisticLockException(
                    aggregateId,
                    expectedVersion,
                    currentVersion
            );
        }

        stream.addAll(events);
    }

    @Override
    public List<DomainEvent> loadEvents(UUID aggregateId) {
        return List.of();
    }

}
