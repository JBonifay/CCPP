package fr.joffreybonifay.ccpp.usermanagement.infrastructure.projection;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.joffreybonifay.ccpp.shared.event.DomainEvent;
import fr.joffreybonifay.ccpp.shared.eventstore.EventEnvelope;
import fr.joffreybonifay.ccpp.usermanagement.domain.event.UserAssignedToWorkspace;
import fr.joffreybonifay.ccpp.usermanagement.domain.event.UserCreated;
import fr.joffreybonifay.ccpp.usermanagement.infrastructure.repository.JpaUserRepository;
import fr.joffreybonifay.ccpp.usermanagement.infrastructure.repository.JpaUserWorkspacesRepository;
import fr.joffreybonifay.ccpp.usermanagement.infrastructure.repository.UserJpaEntity;
import fr.joffreybonifay.ccpp.usermanagement.infrastructure.repository.UserWorkspacesJpaEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;

@Slf4j
public class UserProjectionUpdater {

    private final JpaUserRepository jpaUserRepository;
    private final JpaUserWorkspacesRepository jpaUserWorkspacesRepository;
    private final ObjectMapper objectMapper;

    public UserProjectionUpdater(
            JpaUserRepository jpaUserRepository,
            JpaUserWorkspacesRepository jpaUserWorkspacesRepository,
            ObjectMapper objectMapper
    ) {
        this.jpaUserRepository = jpaUserRepository;
        this.jpaUserWorkspacesRepository = jpaUserWorkspacesRepository;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "user-management-events", groupId = "user-projection-group")
    public void onMessage(String message) {
        try {
            EventEnvelope envelope = objectMapper.readValue(message, EventEnvelope.class);
            Class<?> eventClass = Class.forName(envelope.eventType());
            DomainEvent event = (DomainEvent) objectMapper.readValue(envelope.payload(), eventClass);

            switch (event) {
                case UserCreated userCreated -> handle(userCreated);
                case UserAssignedToWorkspace userAssignedToWorkspace -> handle(userAssignedToWorkspace);
                default -> log.debug("Ignoring non-saga event: {}", event.getClass().getSimpleName());
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
        jpaUserRepository.save(entity);
    }

    private void handle(UserAssignedToWorkspace userAssignedToWorkspace) {
        jpaUserWorkspacesRepository.save(
                new UserWorkspacesJpaEntity(
                        userAssignedToWorkspace.userId().value(),
                        userAssignedToWorkspace.workspaceId().value()
                )
        );
    }

}
