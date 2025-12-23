package fr.joffreybonifay.ccpp.shared.eventstore.impl;

import fr.joffreybonifay.ccpp.shared.eventstore.AggregateType;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Entity
@Table(name = "event_stream",
        indexes = {
                @Index(name = "idx_event_stream_aggregate", columnList = "aggregate_id"),
                @Index(name = "idx_event_stream_correlation", columnList = "correlation_id"),
                @Index(name = "idx_event_stream_command", columnList = "command_id")
        })
public class EventStreamEntry {

    // Getters
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "aggregate_id", nullable = false)
    private UUID aggregateId;

    @Enumerated(EnumType.STRING)
    @Column(name = "aggregate_type", nullable = false)
    private AggregateType aggregateType;

    @Column(nullable = false)
    private Long version;

    @Column(name = "event_type", nullable = false)
    private String eventType;

    @Column(name = "event_data", nullable = false, columnDefinition = "TEXT")
    private String eventData;

    @Column(name = "occurred_on", nullable = false)
    private Instant occurredOn;

    // Infrastructure metadata (NOT in domain!)
    @Column(name = "command_id", nullable = false)
    private UUID commandId;

    @Column(name = "correlation_id", nullable = false)
    private UUID correlationId;

    @Column(name = "causation_id", nullable = false)
    private UUID causationId;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = Instant.now();
        }
        // Auto-generate version
        if (version == null) {
            version = 1L; // Will be updated by query
        }
    }

    protected EventStreamEntry() {}

    public EventStreamEntry(
            UUID aggregateId,
            AggregateType aggregateType,
            String eventType,
            String eventData,
            Instant occurredOn,
            UUID correlationId,
            UUID causationId,
            UUID commandId
    ) {
        this.aggregateId = aggregateId;
        this.aggregateType = aggregateType;
        this.eventType = eventType;
        this.eventData = eventData;
        this.occurredOn = occurredOn;
        this.correlationId = correlationId;
        this.causationId = causationId;
        this.commandId = commandId;
    }

}
