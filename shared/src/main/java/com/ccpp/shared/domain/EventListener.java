package com.ccpp.shared.domain;

public interface EventListener {
    void onEvent(DomainEvent event);
    boolean canHandle(DomainEvent event);
}
