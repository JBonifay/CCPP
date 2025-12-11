package com.ccpp.shared.domain;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@EqualsAndHashCode
@ToString
public abstract class DomainEvent {

    protected UUID aggregateId;
    protected Instant occurredOn;
    protected int version;

    protected DomainEvent() {
    }

    public DomainEvent(UUID aggregateId, Instant occurredOn, int version) {
        this.aggregateId = aggregateId;
        this.occurredOn = occurredOn;
        this.version = version;
    }

}

