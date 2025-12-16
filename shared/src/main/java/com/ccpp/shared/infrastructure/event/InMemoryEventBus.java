package com.ccpp.shared.infrastructure.event;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class InMemoryEventBus implements EventBus {

    private final Map<Class<DomainEvent>, List<EventHandler>> handlers = new HashMap<>();

    public void subscribe(Class<DomainEvent> eventType, EventHandler handler) {
        handlers.computeIfAbsent(eventType, k -> new ArrayList<>()).add(handler);
    }

    public void publish(DomainEvent event) {
        Class<? extends DomainEvent> eventType = event.getClass();
        List<EventHandler> eventHandlers = handlers.getOrDefault(eventType, new ArrayList<>());
        for (EventHandler handler : eventHandlers) {
            handler.handle(event);
        }
    }}
