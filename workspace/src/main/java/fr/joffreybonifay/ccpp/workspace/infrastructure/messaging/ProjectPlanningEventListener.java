package fr.joffreybonifay.ccpp.workspace.infrastructure.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.joffreybonifay.ccpp.shared.command.CommandBus;
import fr.joffreybonifay.ccpp.shared.domain.event.DomainEvent;
import fr.joffreybonifay.ccpp.shared.domain.event.ProjectCreationRequested;
import fr.joffreybonifay.ccpp.shared.eventstore.EventEnvelope;
import fr.joffreybonifay.ccpp.shared.indempotency.ProcessedEventEntity;
import fr.joffreybonifay.ccpp.shared.indempotency.ProcessedEventRepository;
import fr.joffreybonifay.ccpp.shared.exception.EventProcessingException;
import fr.joffreybonifay.ccpp.workspace.application.command.command.ApproveProjectCreationCommand;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;

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

    @KafkaListener(topics = "project-planning-events")
    public void listen(String message) {
        try {
            EventEnvelope envelope = objectMapper.readValue(message, EventEnvelope.class);
            log.info("Received event: {} (correlationId: {}, aggregateType: {})", envelope.eventType(), envelope.correlationId(), envelope.aggregateType());

            if (processedEventRepository.existsById(envelope.eventId())) {
                log.info("Event {} already processed, skipping", envelope.eventId());
                return;
            }

            Class<?> eventClass = Class.forName(envelope.eventType());
            DomainEvent event = (DomainEvent) objectMapper.readValue(envelope.payload(), eventClass);

            // React to workspace events by dispatching commands (choreography)
            switch (event) {
                case ProjectCreationRequested e -> handle(e, envelope);
                default -> log.debug("Ignoring non-saga event: {}", event.getClass().getSimpleName());
            }

            // Mark as processed
            processedEventRepository.save(
                    new ProcessedEventEntity(envelope.eventId(), envelope.eventType(), Instant.now())
            );

        } catch (ClassNotFoundException e) {
            log.warn("Unknown event type while processing workspace event: {}", e.getMessage());
            // Don't rethrow - skip unknown event types to avoid blocking the consumer
        } catch (Exception e) {
            log.error("Failed to process workspace event", e);
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
