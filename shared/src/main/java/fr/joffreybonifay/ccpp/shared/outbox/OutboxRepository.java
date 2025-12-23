package fr.joffreybonifay.ccpp.shared.outbox;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OutboxRepository extends JpaRepository<OutboxEntry, Long> {
    List<OutboxEntry> findTop100ByProcessedFalseOrderByIdAsc();
    List<OutboxEntry> findByProcessed(boolean processed);

}
