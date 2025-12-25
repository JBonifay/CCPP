package fr.joffreybonifay.ccpp.shared.indempotency;

import jakarta.persistence.*;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Entity
@Table(
        name = "processed_events",
        uniqueConstraints = @UniqueConstraint(columnNames = "eventId")
)
public class ProcessedEventEntity {

    @Id
    private UUID eventId;

    @Column(nullable = false)
    private String eventType;

    @Column(nullable = false, updatable = false)
    private Instant processedAt;

    @PrePersist
    void onPersist() {
        if (processedAt == null) {
            processedAt = Instant.now();
        }
    }

    public ProcessedEventEntity() {
    }

    public ProcessedEventEntity(UUID eventId, String eventType) {
        this.eventId = eventId;
        this.eventType = eventType;
    }

}
