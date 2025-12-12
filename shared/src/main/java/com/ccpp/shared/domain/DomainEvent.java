package com.ccpp.shared.domain;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@EqualsAndHashCode
@ToString
public abstract class DomainEvent {

    private final Integer eventSequence;

    protected DomainEvent(Integer eventSequence) {
        this.eventSequence = eventSequence;
    }

}

