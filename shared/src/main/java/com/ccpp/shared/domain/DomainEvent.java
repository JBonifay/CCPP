package com.ccpp.shared.domain;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
public abstract class DomainEvent {

    protected UUID aggregateId;
    protected Instant occurredOn;
    protected int version;

    protected DomainEvent() {
    }

}

