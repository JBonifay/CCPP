package fr.joffreybonifay.ccpp.shared.outbox;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.joffreybonifay.ccpp.shared.eventstore.EventEnvelope;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;

public class OutboxWorker {

    private final OutboxRepository outboxRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private final String topic;

    public OutboxWorker(
            OutboxRepository outboxRepository,
            KafkaTemplate<String, String> kafkaTemplate,
            ObjectMapper objectMapper,
            String topic
    ) {
        this.outboxRepository = outboxRepository;
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
        this.topic = topic;
    }

    @Transactional
    public void process(OutboxEntity entity) {
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
                            topic,
                            entity.getAggregateId().toString(),
                            message
                    )
                    .get(); // wait for ACK

            entity.setProcessed(true);
            outboxRepository.save(entity);

        } catch (Exception e) {
            throw new RuntimeException(
                    "Failed to publish outbox event " + entity.getEventId(), e
            );
        }
    }
}
