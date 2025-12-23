package fr.joffreybonifay.ccpp.shared.outbox;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.joffreybonifay.ccpp.shared.eventstore.EventEnvelope;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
public class OutboxProcessor {

    private final OutboxRepository outboxRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private final String topic;

    public OutboxProcessor(
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

    @Scheduled(fixedDelayString = "${outbox.poll-delay-ms:1000}")
    public void processOutbox() {
        try {
            List<OutboxEntry> batch = outboxRepository
                    .findTop100ByProcessedFalseOrderByIdAsc();

            if (!batch.isEmpty()) {
                log.debug("Processing {} outbox entries", batch.size());
            }

            for (OutboxEntry entry : batch) {
                processEntry(entry);
            }

        } catch (Exception e) {
            log.error("Error in outbox processor", e);
        }
    }

    @Transactional
    protected void processEntry(OutboxEntry entry) {
        try {
            log.debug("Publishing event: {} - correlationId: {}, commandId: {}",
                    entry.getEventId(), entry.getCorrelationId(), entry.getCommandId());

            // Build envelope with tracing metadata
            EventEnvelope envelope = new EventEnvelope(
                    entry.getEventId(),
                    entry.getAggregateId(),
                    entry.getAggregateType(),
                    entry.getVersion(),
                    entry.getEventType(),
                    entry.getCommandId(),
                    entry.getCorrelationId(),
                    entry.getCausationId(),
                    entry.getTimestamp(),
                    entry.getPayload()
            );

            String message = objectMapper.writeValueAsString(envelope);

            kafkaTemplate.send(topic, entry.getAggregateId().toString(), message).get(5, TimeUnit.SECONDS);

            entry.setProcessed(true);
            entry.setProcessedAt(Instant.now());
            outboxRepository.save(entry);

            log.info("Published event {} to Kafka - correlationId: {}", entry.getEventId(), entry.getCorrelationId());

        } catch (Exception e) {
            handleFailure(entry, e);
        }
    }

    private void handleFailure(OutboxEntry entry, Exception error) {
        entry.incrementRetryCount();
        entry.setLastError(truncate(error.getMessage(), 1000));

        if (entry.getRetryCount() >= 3) {
            entry.setFailed(true);
            log.error("Event {} FAILED after 3 retries - correlationId: {}",
                    entry.getEventId(), entry.getCorrelationId(), error);
        } else {
            log.warn("Failed to publish event {} (attempt {}/3) - correlationId: {}",
                    entry.getEventId(), entry.getRetryCount(), entry.getCorrelationId());
        }

        outboxRepository.save(entry);
    }

    private String truncate(String str, int maxLength) {
        if (str == null) return null;
        return str.length() > maxLength ? str.substring(0, maxLength) + "..." : str;
    }
}
