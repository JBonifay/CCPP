package com.ccpp.shared.outbox;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "outbox")
public class OutboxEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private UUID eventId;
    private UUID aggregateId;
    private String aggregateType;
    private Long version;
    private String eventType;

    @Column(columnDefinition = "TEXT")
    private String payload;

    private Instant timestamp;
    private UUID correlationId;
    private UUID causationId;
    private boolean processed = false;

    public OutboxEntity() {}

    public OutboxEntity(UUID eventId, UUID aggregateId, String aggregateType, Long version, String eventType, String payload, Instant timestamp, UUID correlationId, UUID causationId) {
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

    public Long getId() { return id; }
    public UUID getEventId() { return eventId; }
    public UUID getAggregateId() { return aggregateId; }
    public String getAggregateType() { return aggregateType; }
    public Long getVersion() { return version; }
    public String getEventType() { return eventType; }
    public String getPayload() { return payload; }
    public Instant getTimestamp() { return timestamp; }
    public UUID getCorrelationId() { return correlationId; }
    public UUID getCausationId() { return causationId; }
    public boolean isProcessed() { return processed; }
    public void setProcessed(boolean processed) { this.processed = processed; }
}
