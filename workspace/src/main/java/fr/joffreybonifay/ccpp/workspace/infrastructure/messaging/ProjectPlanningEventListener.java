package fr.joffreybonifay.ccpp.workspace.infrastructure.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.joffreybonifay.ccpp.shared.command.CommandBus;
import fr.joffreybonifay.ccpp.shared.domain.event.DomainEvent;
import fr.joffreybonifay.ccpp.shared.domain.event.ProjectCreationRequested;
import fr.joffreybonifay.ccpp.shared.eventstore.EventEnvelope;
import fr.joffreybonifay.ccpp.shared.indempotency.ProcessedEventRepository;
import fr.joffreybonifay.ccpp.shared.messaging.AbstractEventListener;
import fr.joffreybonifay.ccpp.workspace.application.command.command.ApproveProjectCreationCommand;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ProjectPlanningEventListener extends AbstractEventListener {

    public ProjectPlanningEventListener(
            ObjectMapper objectMapper,
            CommandBus commandBus,
            ProcessedEventRepository processedEventRepository
    ) {
        super(objectMapper, commandBus, processedEventRepository);
    }

    @KafkaListener(topics = "project-planning-events")
    public void listen(String message) {
        processMessage(message);
    }

    @Override
    protected void handleEvent(DomainEvent event, EventEnvelope envelope) {
        switch (event) {
            case ProjectCreationRequested e -> handle(e, envelope);
            default -> log.debug("Ignoring non-saga event: {}", event.getClass().getSimpleName());
        }
    }

    private void handle(ProjectCreationRequested event, EventEnvelope envelope) {
        commandBus.execute(new ApproveProjectCreationCommand(
                event.workspaceId(),
                event.projectId(),
                envelope.correlationId(),
                envelope.causationId()
        ));
    }
}
