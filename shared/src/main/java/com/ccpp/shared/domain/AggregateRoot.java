package com.ccpp.shared.domain;

import com.ccpp.shared.infrastructure.event.DomainEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public abstract class AggregateRoot {

    protected final List<DomainEvent> uncommittedEvents = new ArrayList<>();
    protected UUID aggregateId;
    protected int version = -1;

    protected abstract void apply(DomainEvent event);

    protected void raiseEvent(DomainEvent event) {
        apply(event);
        uncommittedEvents.add(event);
    }

    public void loadFromHistory(List<? extends DomainEvent> history) {
        for (DomainEvent event : history) {
            apply(event);
            version++;
        }
    }

    public int version() {
        return version;
    }

    public List<DomainEvent> uncommittedEvents() {
        return List.copyOf(uncommittedEvents);
    }

    public void markEventsAsCommitted() {
        version += uncommittedEvents.size();
        uncommittedEvents.clear();
    }

}
