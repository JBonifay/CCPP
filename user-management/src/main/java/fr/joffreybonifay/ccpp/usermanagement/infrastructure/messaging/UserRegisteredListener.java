package fr.joffreybonifay.ccpp.usermanagement.infrastructure.messaging;

import fr.joffreybonifay.ccpp.shared.event.UserRegisteredEvent;
import fr.joffreybonifay.ccpp.usermanagement.application.command.UserRegisteredHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class UserRegisteredListener {

    private final UserRegisteredHandler handler;

    public UserRegisteredListener(UserRegisteredHandler handler) {
        this.handler = handler;
    }

    @KafkaListener(topics = "user-registered-events")
    public void listen(UserRegisteredEvent event) {
        handler.handle(event);
    }
}
