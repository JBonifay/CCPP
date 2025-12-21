package fr.joffreybonifay.ccpp.usermanagement.infrastructure.projection;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.joffreybonifay.ccpp.shared.event.DomainEvent;
import fr.joffreybonifay.ccpp.shared.eventstore.EventEnvelope;
import fr.joffreybonifay.ccpp.usermanagement.domain.event.UserCreated;
import fr.joffreybonifay.ccpp.usermanagement.infrastructure.repository.UserJpaEntity;
import fr.joffreybonifay.ccpp.usermanagement.infrastructure.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;

@Slf4j
public class UserProjectionUpdater {

    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;

    public UserProjectionUpdater(UserRepository userRepository, ObjectMapper objectMapper) {
        this.userRepository = userRepository;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "user-management-events", groupId = "user-projection-group")
    public void onMessage(String message) {
        try {
            EventEnvelope envelope = objectMapper.readValue(message, EventEnvelope.class);
            Class<?> eventClass = Class.forName(envelope.eventType());
            DomainEvent event = (DomainEvent) objectMapper.readValue(envelope.payload(), eventClass);

            if (event instanceof UserCreated userCreated) {
                handle(userCreated);
            }
        } catch (Exception e) {
            log.error("Error processing user projection", e);
        }
    }

    private void handle(UserCreated event) {
        UserJpaEntity entity = new UserJpaEntity(
                event.userId().value(),
                event.email().value(),
                event.passwordHash(),
                event.fullname()
        );
        userRepository.save(entity);
    }
}
