package fr.joffreybonifay.ccpp.shared.aggregate;

import fr.joffreybonifay.ccpp.shared.event.DomainEvent;

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

    public UUID aggregateId() {
        return aggregateId;
    }

    public int version() {
        return version;
    }

    public List<DomainEvent> uncommittedCHanges() {
        return List.copyOf(uncommittedEvents);
    }

    public void markEventsAsCommitted() {
        version += uncommittedEvents.size();
        uncommittedEvents.clear();
    }

}
