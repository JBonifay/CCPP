package com.ccpp.shared.eventbus;

import com.ccpp.shared.event.DomainEvent;

import java.util.List;

public interface EventBus {
    void publish(List<DomainEvent> events);
}
