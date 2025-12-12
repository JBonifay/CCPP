package com.ccpp.shared.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public abstract class AggregateRoot {

    protected final List<DomainEvent> uncommittedEvents = new ArrayList<>();
    protected UUID aggregateId;
    protected int version = -1;

    protected abstract void apply(DomainEvent event);

    protected void raiseEvent(DomainEvent event) {
        uncommittedEvents.add(event);
        apply(event);
    }

    public List<DomainEvent> uncommittedEvents() {
        return List.copyOf(uncommittedEvents);
    }

    public void clearUncommittedEvents() {
        this.uncommittedEvents.clear();
    }

    public void loadFromHistory(List<DomainEvent> events) {
        events.forEach(this::apply);
    }

    protected Integer nextEventSequenceNumber() {
        return ++version;
    }

}
