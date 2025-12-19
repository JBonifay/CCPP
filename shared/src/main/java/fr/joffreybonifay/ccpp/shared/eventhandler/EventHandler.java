package fr.joffreybonifay.ccpp.shared.eventhandler;

import fr.joffreybonifay.ccpp.shared.event.DomainEvent;

@FunctionalInterface
public interface EventHandler {
    void handle(DomainEvent event);
}
