package fr.joffreybonifay.ccpp.shared.eventbus;

import fr.joffreybonifay.ccpp.shared.event.DomainEvent;

import java.util.List;

public interface EventBus {
    void publish(List<DomainEvent> events);
}
