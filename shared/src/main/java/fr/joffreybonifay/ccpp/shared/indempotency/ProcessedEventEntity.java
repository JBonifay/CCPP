package fr.joffreybonifay.ccpp.shared.indempotency;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Entity
@Table(name = "processed_events")
public class ProcessedEventEntity {

    @Id
    private UUID eventId;

    @Column(nullable = false)
    private String eventType;

    @Column(nullable = false)
    private Instant processedAt;

    public ProcessedEventEntity() {
    }

    public ProcessedEventEntity(UUID eventId, String eventType, Instant processedAt) {
        this.eventId = eventId;
        this.eventType = eventType;
        this.processedAt = processedAt;
    }

}
