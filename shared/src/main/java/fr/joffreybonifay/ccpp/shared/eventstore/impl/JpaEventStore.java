package fr.joffreybonifay.ccpp.shared.eventstore.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.joffreybonifay.ccpp.shared.domain.event.DomainEvent;
import fr.joffreybonifay.ccpp.shared.eventstore.AggregateType;
import fr.joffreybonifay.ccpp.shared.eventstore.EventMetadata;
import fr.joffreybonifay.ccpp.shared.eventpublisher.EventPublisher;
import fr.joffreybonifay.ccpp.shared.eventstore.EventStore;
import fr.joffreybonifay.ccpp.shared.exception.OptimisticLockException;
import fr.joffreybonifay.ccpp.shared.outbox.OutboxEntry;
import fr.joffreybonifay.ccpp.shared.outbox.OutboxRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Slf4j
@Repository
public class JpaEventStore implements EventStore {

    private final EventStreamRepository eventStreamRepository;
    private final OutboxRepository outboxRepository;
    private final ObjectMapper objectMapper;
    private final EventPublisher eventPublisher;

    public JpaEventStore(EventStreamRepository eventStreamRepository, OutboxRepository outboxRepository, ObjectMapper objectMapper, EventPublisher eventPublisher) {
        this.eventStreamRepository = eventStreamRepository;
        this.outboxRepository = outboxRepository;
        this.objectMapper = objectMapper;
        this.eventPublisher = eventPublisher;
    }

    @Override
    @Transactional
    public void saveEvents(UUID aggregateId, AggregateType aggregateType, List<EventMetadata> eventsWithMetadata, int expectedVersion) {
        long currentVersion = eventStreamRepository
                .findMaxVersionByAggregateId(aggregateId)
                .orElse(-1L);

        if (currentVersion != expectedVersion) {
            throw new OptimisticLockException(
                    "Expected version " + expectedVersion + " but was " + currentVersion
            );
        }

        long nextVersion = currentVersion;

        for (EventMetadata metadata : eventsWithMetadata) {
            DomainEvent domainEvent = metadata.domainEvent();
            nextVersion++;

            String eventData = serializeEvent(domainEvent);

            // 1. Save to event_stream (source of truth)
            EventStreamEntry streamEntry = new EventStreamEntry(
                    aggregateId,
                    aggregateType,
                    nextVersion,
                    domainEvent.getClass().getName(),
                    eventData,
                    Instant.now(),
                    metadata.correlationId(),
                    metadata.causationId(),
                    metadata.commandId()
            );
            eventStreamRepository.save(streamEntry);

            // 2. Save to outbox (for Kafka publishing) - SAME TRANSACTION
            OutboxEntry outboxEntry = new OutboxEntry(
                    metadata.eventId(),
                    aggregateId,
                    aggregateType,
                    streamEntry.getVersion(),
                    domainEvent.getClass().getName(),
                    metadata.correlationId(),
                    metadata.causationId(),
                    metadata.commandId(),
                    metadata.occurredOn(),
                    eventData
            );
            outboxRepository.save(outboxEntry);

            // 3. ✅ Publish to Spring @EventListener (for LOCAL projections)
            // This happens SYNCHRONOUSLY in the SAME transaction
            eventPublisher.publish(domainEvent);

            log.debug("Published domain event to local listeners: {}",
                    domainEvent.getClass().getSimpleName());
        }

        log.info("Saved and published {} events for aggregate {} (type: {})",
                eventsWithMetadata.size(), aggregateId, aggregateType);
    }

    @Override
    public List<DomainEvent> loadEvents(UUID aggregateId) {
        return eventStreamRepository.findByAggregateIdOrderByVersion(aggregateId)
                .stream()
                .map(this::deserializeEvent)
                .toList();
    }

    private String serializeEvent(DomainEvent event) {
        try {
            return objectMapper.writeValueAsString(event);
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize event", e);
        }
    }

    private DomainEvent deserializeEvent(EventStreamEntry entry) {
        try {
            Class<?> eventClass = Class.forName(entry.getEventType());
            return (DomainEvent) objectMapper.readValue(entry.getEventData(), eventClass);
        } catch (Exception e) {
            throw new RuntimeException("Failed to deserialize event", e);
        }
    }


}
