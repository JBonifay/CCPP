package fr.joffreybonifay.ccpp.shared.outbox;

import org.springframework.scheduling.annotation.Scheduled;

import java.util.List;

public class OutboxProcessor {

    private final OutboxWorker worker;
    private final OutboxRepository outboxRepository;

    public OutboxProcessor(
            OutboxWorker worker,
            OutboxRepository outboxRepository
    ) {
        this.worker = worker;
        this.outboxRepository = outboxRepository;
    }

    @Scheduled(fixedDelayString = "${outbox.poll-delay-ms:1000}")
    public void processOutbox() {
        List<OutboxEntity> batch = outboxRepository.findTop100ByProcessedFalseOrderByIdAsc();

        for (OutboxEntity entity : batch) {
            worker.process(entity);
        }
    }

}
