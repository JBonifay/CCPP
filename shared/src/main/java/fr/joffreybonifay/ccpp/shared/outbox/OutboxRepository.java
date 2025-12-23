package fr.joffreybonifay.ccpp.shared.outbox;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.List;

@Repository
public interface OutboxRepository extends JpaRepository<OutboxEntry, Long> {
    List<OutboxEntry> findTop100ByProcessedFalseOrderByIdAsc();
    List<OutboxEntry> findByProcessed(boolean processed);
}
