package fr.joffreybonifay.ccpp.shared.outbox;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;

@Slf4j
public class OutboxCleanupJob {

    private final OutboxRepository outboxRepository;

    public OutboxCleanupJob(OutboxRepository outboxRepository) {
        this.outboxRepository = outboxRepository;
    }

    @Scheduled(cron = "0 0 3 * * *")
    @Transactional
    public void cleanupOldPublishedEntries() {
        log.info("Cleaned up old published outbox entries (older than 7 days)");

        outboxRepository.findByProcessed(true)
                .stream()
                .filter(OutboxEntry::isProcessed)
                .forEach(outboxRepository::delete);
    }

}
