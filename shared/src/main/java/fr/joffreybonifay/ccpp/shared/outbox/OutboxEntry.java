package fr.joffreybonifay.ccpp.shared.outbox;

import fr.joffreybonifay.ccpp.shared.eventstore.AggregateType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Entity
@Table(name = "outbox",
        indexes = {
                @Index(name = "idx_outbox_processed", columnList = "processed"),
                @Index(name = "idx_outbox_correlation", columnList = "correlation_id"),
                @Index(name = "idx_outbox_command", columnList = "command_id")
        })
public class OutboxEntry {

    // Getters and setters
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "event_id", nullable = false, unique = true)
    private UUID eventId;

    @Column(name = "aggregate_id", nullable = false)
    private UUID aggregateId;

    @Enumerated(EnumType.STRING)
    @Column(name = "aggregate_type", nullable = false)
    private AggregateType aggregateType;

    @Column(nullable = false)
    private Long version;

    @Column(name = "event_type", nullable = false)
    private String eventType;

    // Infrastructure metadata
    @Column(name = "correlation_id", nullable = false)
    private UUID correlationId;

    @Column(name = "causation_id", nullable = false)
    private UUID causationId;

    @Column(name = "command_id", nullable = false)
    private UUID commandId;

    @Column(nullable = false)
    private Instant timestamp;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String payload;

    @Column(nullable = false)
    @Setter
    private boolean processed = false;

    @Column(name = "processed_at")
    @Setter
    private Instant processedAt;

    @Column(nullable = false)
    @Setter
    private boolean failed = false;

    @Column(name = "retry_count", nullable = false)
    private int retryCount = 0;

    @Setter
    @Column(name = "last_error", columnDefinition = "TEXT")
    private String lastError;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = Instant.now();
        }
    }

    protected OutboxEntry() {}

    public OutboxEntry(
            UUID eventId,
            UUID aggregateId,
            AggregateType aggregateType,
            Long version,
            String eventType,
            UUID correlationId,
            UUID causationId,
            UUID commandId,
            Instant timestamp,
            String payload
    ) {
        this.eventId = eventId;
        this.aggregateId = aggregateId;
        this.aggregateType = aggregateType;
        this.version = version;
        this.eventType = eventType;
        this.correlationId = correlationId;
        this.causationId = causationId;
        this.commandId = commandId;
        this.timestamp = timestamp;
        this.payload = payload;
    }


    public void incrementRetryCount() { this.retryCount++; }

}
