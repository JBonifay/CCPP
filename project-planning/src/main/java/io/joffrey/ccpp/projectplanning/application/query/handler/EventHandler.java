package io.joffrey.ccpp.projectplanning.application.query.handler;

import com.ccpp.shared.domain.DomainEvent;

public interface EventHandler<T extends DomainEvent> {
      void handle(T event);
}
