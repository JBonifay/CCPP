package fr.joffreybonifay.ccpp.shared.outbox;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Entity
@Table(
        name = "outbox",
        indexes = {
                @Index(name = "idx_outbox_processed", columnList = "processed"),
                @Index(name = "idx_outbox_event_id", columnList = "eventId")
        }
)
public class OutboxEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long id;

    @Column
    private UUID eventId;

    @Column
    private UUID aggregateId;

    @Column
    private String aggregateType;

    @Column
    private Long version;

    @Column
    private String eventType;

    @Column(columnDefinition = "TEXT")
    private String payload;

    @Column
    private Instant timestamp;

    @Column
    private UUID correlationId;

    @Column
    private UUID causationId;

    @Setter
    @Column
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

}
