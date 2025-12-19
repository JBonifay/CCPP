package com.ccpp.shared.outbox;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OutboxRepository extends JpaRepository<OutboxEntity, Long> {
    List<OutboxEntity> findTop100ByProcessedFalseOrderByIdAsc();
}
