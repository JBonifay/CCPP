package com.ccpp.shared.eventstore;

import com.ccpp.shared.event.DomainEvent;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.UUID;


@Getter
@EqualsAndHashCode
@ToString
public class EventEnvelope {
    private final DomainEvent event;
    private final UUID eventId;
    private final UUID correlationId;
    private final UUID causationId;

    public EventEnvelope(DomainEvent event, UUID correlationId, UUID causationId) {
        this.event = event;
        this.eventId = UUID.randomUUID();
        this.correlationId = correlationId;
        this.causationId = causationId;
    }

}
