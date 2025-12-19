package fr.joffreybonifay.ccpp.shared.outbox;

import fr.joffreybonifay.ccpp.shared.eventstore.EventEnvelope;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public class OutboxProcessor {

    private static final String TOPIC = "project-events";

    private final OutboxRepository outboxRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public OutboxProcessor(
            OutboxRepository outboxRepository,
            KafkaTemplate<String, String> kafkaTemplate,
            ObjectMapper objectMapper
    ) {
        this.outboxRepository = outboxRepository;
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    @Scheduled(fixedDelayString = "${outbox.poll-delay-ms:1000}")
    @Transactional
    public void processOutbox() {

        List<OutboxEntity> batch = outboxRepository.findTop100ByProcessedFalseOrderByIdAsc();

        for (OutboxEntity entity : batch) {
            publish(entity);
            entity.setProcessed(true);
            outboxRepository.save(entity);
        }
    }

    private void publish(OutboxEntity entity) {
        try {
            EventEnvelope envelope = new EventEnvelope(
                            entity.getEventId(),
                            entity.getAggregateId(),
                            entity.getAggregateType(),
                            entity.getVersion(),
                            entity.getEventType(),
                            entity.getCorrelationId(),
                            entity.getCausationId(),
                            entity.getTimestamp(),
                            entity.getPayload()
                    );

            String message = objectMapper.writeValueAsString(envelope);

            kafkaTemplate.send(
                    TOPIC,
                    entity.getAggregateId().toString(), // partition key
                    message
            );

        } catch (Exception e) {
            // ❌ DO NOT mark processed
            // ❌ DO NOT swallow silently
            throw new RuntimeException(
                    "Failed to publish outbox event " + entity.getEventId(), e
            );
        }
    }
}
