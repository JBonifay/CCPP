package com.ccpp.shared.infrastructure.event;

import com.ccpp.shared.domain.exception.OptimisticLockException;

import java.util.*;

public class InMemoryEventStore implements EventStore {

    private final Map<UUID, List<DomainEvent>> streams = new HashMap<>();

    @Override
    public List<DomainEvent> loadEvents(UUID aggregateId) {
        return streams.getOrDefault(aggregateId, List.of());
    }

    @Override
    public synchronized void saveEvents(
            UUID aggregateId,
            List<DomainEvent> events,
            int expectedVersion
    ) {
        List<DomainEvent> stream = streams.computeIfAbsent(aggregateId, id -> new ArrayList<>());

        int currentVersion = stream.size() - 1;

        if (currentVersion != expectedVersion) {
            throw new OptimisticLockException(
                    aggregateId,
                    expectedVersion,
                    currentVersion
            );
        }

        stream.addAll(events);
    }
}
