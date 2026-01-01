package fr.joffreybonifay.ccpp.usermanagement.infrastructure.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.joffreybonifay.ccpp.shared.command.CommandBus;
import fr.joffreybonifay.ccpp.shared.domain.event.DomainEvent;
import fr.joffreybonifay.ccpp.shared.domain.event.WorkspaceCreated;
import fr.joffreybonifay.ccpp.shared.eventstore.EventEnvelope;
import fr.joffreybonifay.ccpp.shared.indempotency.ProcessedEventRepository;
import fr.joffreybonifay.ccpp.shared.messaging.AbstractEventListener;
import fr.joffreybonifay.ccpp.usermanagement.application.command.AssignUserToWorkspaceCommand;
import fr.joffreybonifay.ccpp.usermanagement.domain.model.UserRole;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
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
            case WorkspaceCreated e -> handle(e, envelope);
            default -> log.debug("Ignoring non-saga event: {}", event.getClass().getSimpleName());
        }
    }

    private void handle(WorkspaceCreated workspaceCreated, EventEnvelope envelope) {
        commandBus.execute(
                new AssignUserToWorkspaceCommand(
                        workspaceCreated.workspaceId(),
                        workspaceCreated.workspaceName(),
                        workspaceCreated.logoUrl(),
                        workspaceCreated.userId(),
                        UserRole.ADMIN,
                        envelope.correlationId(),
                        envelope.causationId()
                ));
    }
}
