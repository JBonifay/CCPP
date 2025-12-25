package fr.joffreybonifay.ccpp.workspace.infrastructure.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.joffreybonifay.ccpp.shared.command.CommandBus;
import fr.joffreybonifay.ccpp.shared.domain.event.DomainEvent;
import fr.joffreybonifay.ccpp.shared.domain.event.ProjectCreationRequested;
import fr.joffreybonifay.ccpp.shared.eventstore.EventEnvelope;
import fr.joffreybonifay.ccpp.shared.exception.EventProcessingException;
import fr.joffreybonifay.ccpp.shared.indempotency.ProcessedEventEntity;
import fr.joffreybonifay.ccpp.shared.indempotency.ProcessedEventRepository;
import fr.joffreybonifay.ccpp.workspace.application.command.command.ApproveProjectCreationCommand;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Slf4j
public class ProjectPlanningEventListener {

    private final ObjectMapper objectMapper;
    private final CommandBus commandBus;
    private final ProcessedEventRepository processedEventRepository;

    public ProjectPlanningEventListener(
            ObjectMapper objectMapper,
            CommandBus commandBus,
            ProcessedEventRepository processedEventRepository
    ) {
        this.objectMapper = objectMapper;
        this.commandBus = commandBus;
        this.processedEventRepository = processedEventRepository;
    }

    @Transactional
    @KafkaListener(topics = "project-planning-events")
    public void listen(String message) {
        EventEnvelope envelope;

        try {
            envelope = objectMapper.readValue(message, EventEnvelope.class);
        } catch (Exception e) {
            throw new EventProcessingException("Invalid event envelope", e);
        }

        try {
            processedEventRepository.save(
                    new ProcessedEventEntity(
                            envelope.eventId(),
                            envelope.eventType()
                    )
            );
        } catch (DataIntegrityViolationException ex) {
            log.info("Event {} already processed, skipping", envelope.eventId());
            return;
        }

        try {
            Class<?> eventClass = Class.forName(envelope.eventType());
            DomainEvent event =
                    (DomainEvent) objectMapper.readValue(envelope.payload(), eventClass);

            switch (event) {
                case ProjectCreationRequested e -> handle(e, envelope);
                default -> log.debug(
                        "Ignoring non-saga event: {}",
                        event.getClass().getSimpleName()
                );
            }

        } catch (ClassNotFoundException e) {
            log.warn("Unknown event type: {}", envelope.eventType());
            // swallow → event is considered processed
        } catch (Exception e) {
            log.error("Failed to process event {}", envelope.eventId(), e);
            throw new EventProcessingException("Failed to process workspace event", e);
        }
    }

    private void handle(ProjectCreationRequested event, EventEnvelope envelope) {
        commandBus.execute(new ApproveProjectCreationCommand(
                event.workspaceId(),
                event.projectId(),
                envelope.correlationId(),
                envelope.causationId()
        ));
    }

}
