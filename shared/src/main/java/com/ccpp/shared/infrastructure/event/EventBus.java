package com.ccpp.shared.infrastructure.event;

public interface EventBus {
    void subscribe(Class<DomainEvent> eventType, EventHandler handler);
    void publish(DomainEvent event);
}
