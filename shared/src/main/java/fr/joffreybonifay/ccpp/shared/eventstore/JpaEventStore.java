package fr.joffreybonifay.ccpp.shared.eventstore;

import fr.joffreybonifay.ccpp.shared.event.DomainEvent;
import fr.joffreybonifay.ccpp.shared.event.EventEntity;
import fr.joffreybonifay.ccpp.shared.event.EventRepository;
import fr.joffreybonifay.ccpp.shared.outbox.OutboxEntity;
import fr.joffreybonifay.ccpp.shared.outbox.OutboxRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

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
        // Fetch events from DB ordered by version
        List<EventEntity> eventEntities = eventRepository.findByAggregateIdOrderByVersionAsc(aggregateId);

        return eventEntities.stream().map(eventEntity -> {
            try {
                Class<?> eventClass = Class.forName(eventEntity.getEventType());
                return (DomainEvent) objectMapper.readValue(eventEntity.getPayload(), eventClass);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException("Unknown event type: " + eventEntity.getEventType(), e);
            } catch (Exception e) {
                throw new RuntimeException("Error deserializing event " + eventEntity.getEventId(), e);
            }
        }).toList();
    }
}
