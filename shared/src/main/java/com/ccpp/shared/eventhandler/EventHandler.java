package com.ccpp.shared.eventhandler;

import com.ccpp.shared.event.DomainEvent;

@FunctionalInterface
public interface EventHandler {
    void handle(DomainEvent event);
}
