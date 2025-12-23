package fr.joffreybonifay.ccpp.shared.eventpublisher;

import fr.joffreybonifay.ccpp.shared.domain.event.DomainEvent;

public interface EventPublisher {
    void publish(DomainEvent domainEvent);
}
