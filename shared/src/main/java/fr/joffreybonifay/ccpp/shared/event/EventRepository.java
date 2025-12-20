package fr.joffreybonifay.ccpp.shared.event;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EventRepository extends JpaRepository<EventEntity, Long> {
    List<EventEntity> findByAggregateIdOrderByVersionAsc(UUID aggregateId);

    @Query("SELECT MAX(e.version) FROM EventEntity e WHERE e.aggregateId = :aggregateId")
    Optional<Long> findMaxVersionByAggregateId(@Param("aggregateId") UUID aggregateId);

    List<EventEntity> findByAggregateId(UUID aggregateId);
}
