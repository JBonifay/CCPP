package fr.joffreybonifay.ccpp.shared.eventstore;

import fr.joffreybonifay.ccpp.shared.event.DomainEvent;
import fr.joffreybonifay.ccpp.shared.eventbus.EventBus;
import fr.joffreybonifay.ccpp.shared.exception.OptimisticLockException;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryEventStore implements EventStore {

    private final Map<UUID, List<DomainEvent>> events = new ConcurrentHashMap<>();

    private final EventBus eventBus;

    public InMemoryEventStore(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    @Override
    public void saveEvents(UUID aggregateId, List<DomainEvent> domainEvents, int expectedVersion, UUID correlationId, UUID causationId) {

        List<DomainEvent> stream = events.computeIfAbsent(aggregateId, id -> new ArrayList<>());
        int currentVersion = stream.size() - 1;

        if (currentVersion != expectedVersion) {
            throw new OptimisticLockException(aggregateId, expectedVersion, currentVersion);
        }

        stream.addAll(domainEvents);
        eventBus.publish(domainEvents);
    }

    @Override
    public List<DomainEvent> loadEvents(UUID aggregateId) {
        return events.getOrDefault(aggregateId, Collections.emptyList());
    }
}
