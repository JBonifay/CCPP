package fr.joffreybonifay.ccpp.shared.eventstore;

import fr.joffreybonifay.ccpp.shared.event.DomainEvent;

import java.util.List;
import java.util.UUID;

public interface EventStore {
    void saveEvents(UUID aggregateId, List<DomainEvent> events, int expectedVersion, UUID correlationId, UUID causationId);
    List<DomainEvent> loadEvents(UUID aggregateId);
}
