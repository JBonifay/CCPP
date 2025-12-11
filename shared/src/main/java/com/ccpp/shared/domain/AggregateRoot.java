package com.ccpp.shared.domain;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public abstract class AggregateRoot {

    protected final UUID aggregateId;
    private int version = -1;
    protected final List<DomainEvent> uncommittedEvents = new ArrayList<>();

    protected AggregateRoot(UUID aggregateId) {
        this.aggregateId = aggregateId;
    }

    protected abstract void apply(DomainEvent event);

    protected void raiseEvent(DomainEvent event) {
        version++;
        event.setAggregateId(aggregateId);
        event.setVersion(version);
        event.setOccurredOn(Instant.now());

        this.uncommittedEvents.add(event);
        apply(event);
    }

    public List<DomainEvent> uncommittedEvents() {
        return List.copyOf(uncommittedEvents);
    }

    public void markEventsAsCommitted() {
        this.uncommittedEvents.clear();
    }

    public void loadFromHistory(List<DomainEvent> history) {
        for (DomainEvent event : history) {
            this.version = event.getVersion();
            apply(event);
        }
    }

}
