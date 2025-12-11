package com.ccpp.shared.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public abstract class AggregateRoot {

    protected final UUID aggregateId;
    protected final List<Object> uncommittedEvents = new ArrayList<>();

    protected AggregateRoot(UUID aggregateId) {
        this.aggregateId = aggregateId;
    }

    protected abstract void apply(Object event);

    protected void raiseEvent(Object event) {
        this.uncommittedEvents.add(event);
        apply(event);
    }

    public List<Object> uncommittedEvents() {
        return List.copyOf(uncommittedEvents);
    }

    public void clearUncommittedEvents() {
        this.uncommittedEvents.clear();
    }

    public void loadFromHistory(List<Object> history) {
        history.forEach(this::apply);
    }

}
