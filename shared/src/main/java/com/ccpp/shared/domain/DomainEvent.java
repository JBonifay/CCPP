package com.ccpp.shared.domain;

public interface DomainEvent {
    void getEventId();
    void getEventType();
    void getAggregateId();
    void getWorkspaceId();
    void getTimestamp();
    void getVersion();
}

