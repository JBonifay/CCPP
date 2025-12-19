package com.ccpp.shared.outbox;

import com.ccpp.shared.event.DomainEvent;
import com.ccpp.shared.eventstore.EventEnvelope;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class OutboxProcessor {

    private final OutboxRepository outboxRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public OutboxProcessor(OutboxRepository outboxRepository, KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
        this.outboxRepository = outboxRepository;
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    @Scheduled(fixedDelay = 1000)
    @Transactional
    public void processOutbox() {
        List<OutboxEntity> entities = outboxRepository.findByProcessedFalseOrderByIdAsc();
        for (OutboxEntity entity : entities) {
            try {
                DomainEvent payload = (DomainEvent) objectMapper.readValue(entity.getPayload(), Class.forName(entity.getEventType()));

                EventEnvelope<DomainEvent> envelope = new EventEnvelope<>(
                        entity.getEventId(),
                        entity.getAggregateId(),
                        entity.getAggregateType(),
                        entity.getVersion(),
                        entity.getEventType(),
                        payload,
                        entity.getTimestamp(),
                        entity.getCorrelationId(),
                        entity.getCausationId()
                );

                String message = objectMapper.writeValueAsString(envelope);
                kafkaTemplate.send("project-events", entity.getEventId().toString(), message);
                entity.setProcessed(true);
                outboxRepository.save(entity);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
