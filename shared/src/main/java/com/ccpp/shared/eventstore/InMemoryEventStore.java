package com.ccpp.shared.eventstore;

import com.ccpp.shared.event.DomainEvent;
import com.ccpp.shared.exception.OptimisticLockException;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryEventStore implements EventStore {

    private final Map<UUID, List<EventEnvelope>> events = new ConcurrentHashMap<>();

    @Override
    public void saveEvents(UUID aggregateId, List<DomainEvent> domainEvents, int expectedVersion, UUID correlationId, UUID causationId) {

        List<EventEnvelope> stream = events.computeIfAbsent(aggregateId, id -> new ArrayList<>());
        int currentVersion = stream.size() - 1;

        if (currentVersion != expectedVersion) {
            throw new OptimisticLockException(aggregateId, expectedVersion, currentVersion);
        }

        domainEvents.forEach(e -> stream.add(new EventEnvelope(e, correlationId, causationId)));
    }

    @Override
    public List<EventEnvelope> loadEvents(UUID aggregateId) {
        return List.copyOf(events.getOrDefault(aggregateId, List.of()));
    }
}
