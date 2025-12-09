package com.ccpp.shared.domain;

import java.util.ArrayList;
import java.util.List;

public abstract class AggregateRoot {

    protected final List<DomainEvent> uncommittedEvents = new ArrayList<>();

    protected void raiseEvent(DomainEvent event) {
        this.uncommittedEvents.add(event);
    }

    protected List<DomainEvent> getUncommittedEvents() {
        return List.copyOf(uncommittedEvents);
    }

    protected void markEventsAsCommitted() {
        this.uncommittedEvents.clear();
    }

}
