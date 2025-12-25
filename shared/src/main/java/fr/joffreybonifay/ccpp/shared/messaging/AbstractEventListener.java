package fr.joffreybonifay.ccpp.shared.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.joffreybonifay.ccpp.shared.command.CommandBus;
import fr.joffreybonifay.ccpp.shared.domain.event.DomainEvent;
import fr.joffreybonifay.ccpp.shared.eventstore.EventEnvelope;
import fr.joffreybonifay.ccpp.shared.exception.EventProcessingException;
import fr.joffreybonifay.ccpp.shared.indempotency.ProcessedEventEntity;
import fr.joffreybonifay.ccpp.shared.indempotency.ProcessedEventRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;

@Slf4j
public abstract class AbstractEventListener {

    protected final ObjectMapper objectMapper;
    protected final CommandBus commandBus;
    protected final ProcessedEventRepository processedEventRepository;

    protected AbstractEventListener(
            ObjectMapper objectMapper,
            CommandBus commandBus,
            ProcessedEventRepository processedEventRepository
    ) {
        this.objectMapper = objectMapper;
        this.commandBus = commandBus;
        this.processedEventRepository = processedEventRepository;
    }

    protected void processMessage(String message) {
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
            DomainEvent event = (DomainEvent) objectMapper.readValue(envelope.payload(), eventClass);

            handleEvent(event, envelope);

        } catch (ClassNotFoundException e) {
            log.warn("Unknown event type: {}", envelope.eventType());
            // swallow → event is considered processed
        } catch (Exception e) {
            log.error("Failed to process event {}", envelope.eventId(), e);
            throw new EventProcessingException("Failed to process workspace event", e);
        }
    }

    protected abstract void handleEvent(DomainEvent event, EventEnvelope envelope);
}
