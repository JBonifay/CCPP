package fr.joffreybonifay.ccpp.shared.eventstore.impl;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EventStreamRepository extends JpaRepository<EventStreamEntry, Long> {

    List<EventStreamEntry> findByAggregateIdOrderByVersion(UUID aggregateId);

    @Query("SELECT MAX(e.version) FROM EventStreamEntry e WHERE e.aggregateId = :aggregateId")
    Optional<Long> findMaxVersionByAggregateId(UUID aggregateId);

}
