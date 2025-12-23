package fr.joffreybonifay.ccpp.shared.eventstore;

import fr.joffreybonifay.ccpp.shared.event.DomainEvent;

import java.util.List;
import java.util.UUID;

public interface EventStore {
    void saveEvents(UUID aggregateId, AggregateType aggregateType, List<EventMetadata> events, int expectedVersion);
    List<DomainEvent> loadEvents(UUID aggregateId);
}
