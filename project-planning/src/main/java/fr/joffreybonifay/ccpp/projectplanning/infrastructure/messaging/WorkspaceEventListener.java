package fr.joffreybonifay.ccpp.projectplanning.infrastructure.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.joffreybonifay.ccpp.projectplanning.application.command.command.ActivateProjectCommand;
import fr.joffreybonifay.ccpp.projectplanning.application.command.command.FailProjectCreationCommand;
import fr.joffreybonifay.ccpp.shared.command.CommandBus;
import fr.joffreybonifay.ccpp.shared.event.DomainEvent;
import fr.joffreybonifay.ccpp.shared.event.WorkspaceProjectCreationApproved;
import fr.joffreybonifay.ccpp.shared.event.WorkspaceProjectCreationRejected;
import fr.joffreybonifay.ccpp.shared.eventstore.EventEnvelope;
import fr.joffreybonifay.ccpp.shared.indempotency.ProcessedEventEntity;
import fr.joffreybonifay.ccpp.shared.indempotency.ProcessedEventRepository;
import fr.joffreybonifay.ccpp.shared.messaging.EventProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
@Slf4j
public class WorkspaceEventListener {

    private final ObjectMapper objectMapper;
    private final CommandBus commandBus;
    private final ProcessedEventRepository processedEventRepository;

    public WorkspaceEventListener(
            ObjectMapper objectMapper,
            CommandBus commandBus,
            ProcessedEventRepository processedEventRepository
    ) {
        this.objectMapper = objectMapper;
        this.commandBus = commandBus;
        this.processedEventRepository = processedEventRepository;
    }

    @KafkaListener(topics = "workspace-events")
    public void listen(String message) {
        try {
            EventEnvelope envelope = objectMapper.readValue(message, EventEnvelope.class);
            log.info("Received workspace event: {} (correlationId: {}, aggregateType: {})", envelope.eventType(), envelope.correlationId(), envelope.aggregateType());

            if (processedEventRepository.existsById(envelope.eventId())) {
                log.info("Event {} already processed, skipping", envelope.eventId());
                return;
            }

            Class<?> eventClass = Class.forName(envelope.eventType());
            DomainEvent event = (DomainEvent) objectMapper.readValue(envelope.payload(), eventClass);

            // React to workspace events by dispatching commands (choreography)
            switch (event) {
                case WorkspaceProjectCreationApproved e -> handleApproved(e, envelope);
                case WorkspaceProjectCreationRejected e -> handleRejected(e, envelope);
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

    private void handleApproved(WorkspaceProjectCreationApproved event, EventEnvelope envelope) {
        log.info("Workspace approved project creation: projectId={}, workspaceId={}", event.projectId(), event.workspaceId());

        commandBus.execute(new ActivateProjectCommand(
                event.projectId(),
                envelope.correlationId(),
                envelope.eventId()
        ));
    }

    private void handleRejected(WorkspaceProjectCreationRejected event, EventEnvelope envelope) {
        log.info("Workspace rejected project creation: projectId={}, workspaceId={}, reason={}", event.projectId(), event.workspaceId(), event.reason());

        commandBus.execute(new FailProjectCreationCommand(
                event.projectId(),
                event.reason(),
                envelope.correlationId(),
                envelope.eventId()
        ));
    }
}
