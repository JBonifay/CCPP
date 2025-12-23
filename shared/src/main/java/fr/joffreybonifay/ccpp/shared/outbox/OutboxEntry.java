package fr.joffreybonifay.ccpp.shared.outbox;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "outbox",
        indexes = {
                @Index(name = "idx_outbox_status", columnList = "status"),
                @Index(name = "idx_outbox_created_at", columnList = "created_at"),
                @Index(name = "idx_outbox_aggregate_id", columnList = "aggregate_id")
        })
public class OutboxEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "event_id", nullable = false, unique = true)
    private UUID eventId;

    @Column(name = "aggregate_id", nullable = false)
    private UUID aggregateId;

    @Column(name = "aggregate_type", nullable = false)
    private String aggregateType;

    @Column(nullable = false)
    private Long version;

    @Column(name = "event_type", nullable = false)
    private String eventType;

    @Column(name = "correlation_id")
    private UUID correlationId;

    @Column(name = "causation_id")
    private UUID causationId;

    @Column(nullable = false)
    private Instant timestamp;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String payload;

    @Column(nullable = false)
    private boolean processed = false;

    @Column(name = "processed_at")
    private Instant processedAt;

    @Column(nullable = false)
    private boolean failed = false;

    @Column(name = "retry_count", nullable = false)
    private int retryCount = 0;

    @Column(name = "last_error", columnDefinition = "TEXT")
    private String lastError;

    @Column(name = "last_retry_at")
    private Instant lastRetryAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    protected OutboxEntry() {
    }

    public OutboxEntry(
            UUID eventId,
            UUID aggregateId,
            String aggregateType,
            Long version,
            String eventType,
            String payload,
            Instant timestamp,
            UUID correlationId,
            UUID causationId) {
        this.eventId = eventId;
        this.aggregateId = aggregateId;
        this.aggregateType = aggregateType;
        this.version = version;
        this.eventType = eventType;
        this.payload = payload;
        this.timestamp = timestamp;
        this.correlationId = correlationId;
        this.causationId = causationId;
    }

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = Instant.now();
        }
        if (timestamp == null) {
            timestamp = Instant.now();
        }
    }

    public void incrementRetryCount() {
        this.retryCount++;
    }

    public boolean shouldRetry(int maxRetries) {
        return !processed && !failed && retryCount < maxRetries;
    }

    public long getAgeInSeconds() {
        return Instant.now().getEpochSecond() - createdAt.getEpochSecond();
    }

}
