package fr.joffreybonifay.ccpp.shared.eventpublisher;

import fr.joffreybonifay.ccpp.shared.domain.event.DomainEvent;
import org.springframework.context.ApplicationEventPublisher;

public class SpringEventPublisher implements EventPublisher {

    private final ApplicationEventPublisher publisher;

    public SpringEventPublisher(ApplicationEventPublisher publisher) {
        this.publisher = publisher;
    }

    @Override
    public void publish(DomainEvent domainEvent) {
        publisher.publishEvent(domainEvent);
    }

}
