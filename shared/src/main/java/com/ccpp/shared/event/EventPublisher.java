package com.ccpp.shared.event;

import com.ccpp.shared.domain.DomainEvent;

/**
 * Publishes domain events to subscribers (in-memory, Kafka, etc.)
 * Used by handlers to notify other bounded contexts about state changes.
 */
public interface EventPublisher {

    /**
     * Publish a domain event to all interested subscribers
     */
    void publish(DomainEvent event);
}