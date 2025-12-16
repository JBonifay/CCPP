package com.ccpp.shared.infrastructure.event;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class SpyEventBus implements EventBus {

    private final List<DomainEvent> sentEvents = new ArrayList<>();

    @Override
    public void subscribe(Class<DomainEvent> eventType, EventHandler handler) {

    }

    @Override
    public void publish(DomainEvent event) {
        sentEvents.add(event);
    }

}
