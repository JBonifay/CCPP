package com.ccpp.shared.eventstore;

import com.ccpp.shared.event.DomainEvent;
import com.ccpp.shared.event.EventEntity;
import com.ccpp.shared.event.EventRepository;
import com.ccpp.shared.outbox.OutboxEntity;
import com.ccpp.shared.outbox.OutboxRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class JpaEventStore implements EventStore {

    private final EventRepository eventRepository;
    private final OutboxRepository outboxRepository;
    private final ObjectMapper objectMapper;

    public JpaEventStore(EventRepository eventRepository, OutboxRepository outboxRepository, ObjectMapper objectMapper) {
        this.eventRepository = eventRepository;
        this.outboxRepository = outboxRepository;
        this.objectMapper = objectMapper;
    }

    @Override
    @Transactional
    public void saveEvents(UUID aggregateId, List<DomainEvent> events, int expectedVersion, UUID correlationId, UUID causationId) {
        // Optimistic concurrency check
        long currentVersion = eventRepository.findMaxVersionByAggregateId(aggregateId).orElse(-1L);

        if (currentVersion != expectedVersion) {
            throw new RuntimeException("Concurrency exception: expected version " + expectedVersion + " but found " + currentVersion);
        }

        long nextVersion = expectedVersion + 1;
        for (DomainEvent event : events) {
            try {
                String payload = objectMapper.writeValueAsString(event);
                String eventType = event.getClass().getName();
                UUID eventId = UUID.randomUUID();
                Instant timestamp = Instant.now();

                EventEntity eventEntity = new EventEntity(
                        eventId,
                        aggregateId,
                        "Project",
                        nextVersion,
                        eventType,
                        payload,
                        timestamp,
                        correlationId,
                        causationId
                );
                eventRepository.save(eventEntity);

                OutboxEntity outboxEntity = new OutboxEntity(
                        eventId,
                        aggregateId,
                        "Project",
                        nextVersion,
                        eventType,
                        payload,
                        timestamp,
                        correlationId,
                        causationId
                );
                outboxRepository.save(outboxEntity);

                nextVersion++;
            } catch (JsonProcessingException e) {
                throw new RuntimeException("Error serializing event", e);
            }
        }
    }

    @Override
    public List<DomainEvent> loadEvents(UUID aggregateId) {
        return null;
    }
}
