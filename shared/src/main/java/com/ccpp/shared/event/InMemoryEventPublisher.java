package com.ccpp.shared.event;

import com.ccpp.shared.domain.DomainEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class InMemoryEventPublisher implements EventPublisher {

    private final ApplicationContext applicationContext;

    public InMemoryEventPublisher(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public void publish(DomainEvent event) {
        applicationContext.publishEvent(event);
    }
}
