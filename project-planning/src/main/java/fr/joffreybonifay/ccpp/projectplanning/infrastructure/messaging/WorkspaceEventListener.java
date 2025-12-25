package fr.joffreybonifay.ccpp.projectplanning.infrastructure.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.joffreybonifay.ccpp.projectplanning.application.command.command.ActivateProjectCommand;
import fr.joffreybonifay.ccpp.projectplanning.application.command.command.FailProjectCreationCommand;
import fr.joffreybonifay.ccpp.shared.command.CommandBus;
import fr.joffreybonifay.ccpp.shared.domain.event.DomainEvent;
import fr.joffreybonifay.ccpp.shared.domain.event.WorkspaceProjectCreationApproved;
import fr.joffreybonifay.ccpp.shared.domain.event.WorkspaceProjectCreationRejected;
import fr.joffreybonifay.ccpp.shared.eventstore.EventEnvelope;
import fr.joffreybonifay.ccpp.shared.indempotency.ProcessedEventRepository;
import fr.joffreybonifay.ccpp.shared.messaging.AbstractEventListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class WorkspaceEventListener extends AbstractEventListener {

    public WorkspaceEventListener(
            ObjectMapper objectMapper,
            CommandBus commandBus,
            ProcessedEventRepository processedEventRepository
    ) {
        super(objectMapper, commandBus, processedEventRepository);
    }

    @KafkaListener(topics = "workspace-events")
    public void listen(String message) {
        processMessage(message);
    }

    @Override
    protected void handleEvent(DomainEvent event, EventEnvelope envelope) {
        switch (event) {
            case WorkspaceProjectCreationApproved e -> handleApproved(e, envelope);
            case WorkspaceProjectCreationRejected e -> handleRejected(e, envelope);
            default -> log.debug("Ignoring non-saga event: {}", event.getClass().getSimpleName());
        }
    }

    private void handleApproved(WorkspaceProjectCreationApproved event, EventEnvelope envelope) {
        log.info("Workspace approved project creation: projectId={}, workspaceId={}",
                event.projectId(), event.workspaceId());

        commandBus.execute(new ActivateProjectCommand(
                event.projectId(),
                envelope.correlationId(),
                envelope.eventId()
        ));
    }

    private void handleRejected(WorkspaceProjectCreationRejected event, EventEnvelope envelope) {
        log.info("Workspace rejected project creation: projectId={}, workspaceId={}, reason={}",
                event.projectId(), event.workspaceId(), event.reason());

        commandBus.execute(new FailProjectCreationCommand(
                event.projectId(),
                event.reason(),
                envelope.correlationId(),
                envelope.eventId()
        ));
    }
}
