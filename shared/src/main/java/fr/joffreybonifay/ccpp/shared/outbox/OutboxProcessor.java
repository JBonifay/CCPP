package fr.joffreybonifay.ccpp.shared.outbox;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.joffreybonifay.ccpp.shared.eventstore.EventEnvelope;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
public class OutboxProcessor {

    private final OutboxRepository outboxRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private final String topic;
    private final int maxRetries;

    public OutboxProcessor(
            OutboxRepository outboxRepository,
            KafkaTemplate<String, String> kafkaTemplate,
            ObjectMapper objectMapper,
            String topic) {
        this.outboxRepository = outboxRepository;
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
        this.topic = topic;
        this.maxRetries = 3; // Could be configurable
    }

    @Scheduled(fixedDelayString = "${outbox.poll-delay-ms:1000}")
    @Transactional
    public void processOutbox() {
        try {
            // Fetch batch of unprocessed entries
            List<OutboxEntry> batch = outboxRepository
                    .findTop100ByProcessedFalseOrderByIdAsc();

            if (batch.isEmpty()) {
                return; // Nothing to process
            }

            log.debug("Processing {} outbox entries", batch.size());

            // Process each entry in its own transaction
            for (OutboxEntry entry : batch) {
                processEntry(entry);
            }

        } catch (Exception e) {
            log.error("Unexpected error in outbox processor", e);
            // Don't rethrow - scheduler will retry on next iteration
        }

    }

    protected void processEntry(OutboxEntry entry) {
        try {
            log.debug("Processing outbox entry {} (attempt {}/{})",
                    entry.getEventId(), entry.getRetryCount() + 1, maxRetries);

            // Build event envelope
            EventEnvelope envelope = new EventEnvelope(
                    entry.getEventId(),
                    entry.getAggregateId(),
                    entry.getAggregateType(),
                    entry.getVersion(),
                    entry.getEventType(),
                    entry.getCorrelationId(),
                    entry.getCausationId(),
                    entry.getTimestamp(),
                    entry.getPayload()
            );

            // Serialize to JSON
            String message = objectMapper.writeValueAsString(envelope);

            // Publish to Kafka with timeout
            kafkaTemplate.send(
                    topic,
                    entry.getAggregateId().toString(),
                    message
            ).get(5, TimeUnit.SECONDS); // 5 second timeout

            // Mark as processed
            entry.setProcessed(true);
            entry.setProcessedAt(java.time.Instant.now());
            outboxRepository.save(entry);

            log.info("Successfully published outbox entry {} to Kafka topic {}",
                    entry.getEventId(), topic);

        } catch (Exception e) {
            handleFailure(entry, e);
        }
    }

    private void handleFailure(OutboxEntry entry, Exception error) {
        entry.incrementRetryCount();
        entry.setLastError(truncateError(error.getMessage(), 1000));
        entry.setLastRetryAt(java.time.Instant.now());

        if (entry.getRetryCount() >= maxRetries) {
            // Max retries exceeded - mark as failed
            entry.setFailed(true);
            log.error("Outbox entry {} FAILED after {} retries. Moving to DLQ. Error: {}",
                    entry.getEventId(), maxRetries, error.getMessage(), error);

            // In production: send to Dead Letter Queue, alert ops team, etc.
            sendToDLQ(entry, error);
        } else {
            // Will retry on next iteration
            log.warn("Failed to publish outbox entry {} (attempt {}/{}): {}",
                    entry.getEventId(), entry.getRetryCount(), maxRetries, error.getMessage());
        }

        outboxRepository.save(entry);
    }

    private void sendToDLQ(OutboxEntry entry, Exception error) {
        try {
            // Option 1: Separate Kafka DLQ topic
            String dlqTopic = topic + ".dlq";
            EventEnvelope envelope = buildEnvelope(entry);
            String message = objectMapper.writeValueAsString(envelope);
            kafkaTemplate.send(dlqTopic, entry.getAggregateId().toString(), message);

            log.info("Sent failed entry {} to DLQ topic {}", entry.getEventId(), dlqTopic);

        } catch (Exception dlqError) {
            // DLQ also failed - log for manual intervention
            log.error("CRITICAL: Failed to send entry {} to DLQ: {}",
                    entry.getEventId(), dlqError.getMessage());

            // In production: alert monitoring system, PagerDuty, etc.
        }
    }

    private EventEnvelope buildEnvelope(OutboxEntry entry) {
        return new EventEnvelope(
                entry.getEventId(),
                entry.getAggregateId(),
                entry.getAggregateType(),
                entry.getVersion(),
                entry.getEventType(),
                entry.getCorrelationId(),
                entry.getCausationId(),
                entry.getTimestamp(),
                entry.getPayload()
        );
    }

    private String truncateError(String error, int maxLength) {
        if (error == null) return null;
        return error.length() > maxLength
                ? error.substring(0, maxLength) + "..."
                : error;
    }
}
