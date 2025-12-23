package fr.joffreybonifay.ccpp.projectplanning.application.command.handler;

import fr.joffreybonifay.ccpp.projectplanning.application.command.command.MarkProjectReadyCommand;
import fr.joffreybonifay.ccpp.projectplanning.domain.event.ProjectMarkedAsReady;
import fr.joffreybonifay.ccpp.shared.domain.event.ProjectCreationRequested;
import fr.joffreybonifay.ccpp.shared.eventbus.EventBus;
import fr.joffreybonifay.ccpp.shared.eventbus.SimpleEventBus;
import fr.joffreybonifay.ccpp.shared.eventstore.AggregateType;
import fr.joffreybonifay.ccpp.shared.eventstore.EventMetadata;
import fr.joffreybonifay.ccpp.shared.eventstore.impl.InMemoryEventStore;
import fr.joffreybonifay.ccpp.shared.domain.identities.ProjectId;
import fr.joffreybonifay.ccpp.shared.domain.identities.UserId;
import fr.joffreybonifay.ccpp.shared.domain.identities.WorkspaceId;
import fr.joffreybonifay.ccpp.shared.domain.valueobjects.DateRange;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class MarkProjectReadyHandlerTest {

    UUID commandId = UUID.randomUUID();
    UUID correlationId = UUID.randomUUID();
    EventBus eventBus = new SimpleEventBus();
    InMemoryEventStore eventStore = new InMemoryEventStore(eventBus);
    MarkProjectReadyHandler handler = new MarkProjectReadyHandler(eventStore);
    WorkspaceId workspaceId = new WorkspaceId(UUID.randomUUID());
    UserId userId = new UserId(UUID.randomUUID());
    ProjectId projectId = new ProjectId(UUID.randomUUID());
    DateRange timeline = new DateRange(LocalDate.of(2025, 1, 1), LocalDate.of(2025, 3, 31));
    String title = "Q1 Video Series";
    String description = "Educational content";
    BigDecimal projectBudgetLimit = BigDecimal.valueOf(1000);

    @Test
    void should_mark_project_as_ready() {
        eventStore.saveEvents(projectId.value(),
                AggregateType.PROJECT_PLANNING,
                List.of(new EventMetadata(new ProjectCreationRequested(projectId, workspaceId, userId, title, description, timeline, projectBudgetLimit), null, null, null)),
                -1);

        handler.handle(new MarkProjectReadyCommand(
                commandId,
                projectId,
                userId,
                correlationId
        ));

        assertThat(eventStore.loadEvents(projectId.value()))
                .last()
                .isEqualTo(
                        new ProjectMarkedAsReady(projectId, workspaceId, userId)
                );
    }

    @Test
    void should_be_idempotent_when_marking_ready_project_as_ready() {
        var projectCreatedEvent = new ProjectCreationRequested(projectId, workspaceId, userId, title, description, timeline, projectBudgetLimit);
        var projectMarkedAsReadyEvent = new ProjectMarkedAsReady(projectId, workspaceId, userId);
        eventStore.saveEvents(projectId.value(),
                AggregateType.PROJECT_PLANNING,
                List.of(
                        new EventMetadata(projectCreatedEvent, null, null, null),
                        new EventMetadata(projectMarkedAsReadyEvent, null, null, null)
                ),
                -1);

        var command = new MarkProjectReadyCommand(
                commandId,
                projectId,
                userId,
                correlationId
        );

        handler.handle(command);

        assertThat(eventStore.loadEvents(projectId.value())).containsExactly(
                projectCreatedEvent,
                projectMarkedAsReadyEvent
        );
    }

}
