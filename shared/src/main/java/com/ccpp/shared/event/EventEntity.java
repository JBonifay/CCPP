package com.ccpp.shared.event;

import jakarta.persistence.*;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Entity
@Table(name = "events", uniqueConstraints = @UniqueConstraint(columnNames = {"aggregateId", "version"}))
public class EventEntity {

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

    public EventEntity() {}

    public EventEntity(UUID eventId, UUID aggregateId, String aggregateType, Long version, String eventType, String payload, Instant timestamp, UUID correlationId, UUID causationId) {
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

}
