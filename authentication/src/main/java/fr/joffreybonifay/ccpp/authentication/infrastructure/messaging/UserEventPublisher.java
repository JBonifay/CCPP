package fr.joffreybonifay.ccpp.authentication.infrastructure.messaging;

import fr.joffreybonifay.ccpp.shared.event.UserRegisteredEvent;

@FunctionalInterface
public interface UserEventPublisher {
    void publish(UserRegisteredEvent event);
}
