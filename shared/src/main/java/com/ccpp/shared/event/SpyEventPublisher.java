package com.ccpp.shared.event;

import com.ccpp.shared.domain.DomainEvent;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class SpyEventPublisher implements EventPublisher {

    private final List<DomainEvent> sentEvents = new ArrayList<>();

    @Override
    public void publish(DomainEvent event) {
        sentEvents.add(event);
    }

}
