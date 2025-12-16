package com.ccpp.shared.infrastructure.event;

@FunctionalInterface
public interface EventHandler {
    void handle(DomainEvent event);
}
