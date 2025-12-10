package com.ccpp.shared.domain;

import java.util.ArrayList;
import java.util.List;

public abstract class AggregateRoot {

    protected final List<DomainEvent> uncommittedEvents = new ArrayList<>();

    protected abstract void apply(DomainEvent event);

    protected void raiseEvent(DomainEvent event) {
        this.uncommittedEvents.add(event);
    }

    public List<DomainEvent> uncommittedEvents() {
        return List.copyOf(uncommittedEvents);
    }

    public void markEventsAsCommitted() {
        this.uncommittedEvents.clear();
    }

}
