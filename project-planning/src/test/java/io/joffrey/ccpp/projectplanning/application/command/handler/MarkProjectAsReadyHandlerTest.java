package io.joffrey.ccpp.projectplanning.application.command.handler;

import com.ccpp.shared.eventbus.EventBus;
import com.ccpp.shared.eventbus.SimpleEventBus;
import com.ccpp.shared.eventstore.InMemoryEventStore;
import com.ccpp.shared.identities.ProjectId;
import com.ccpp.shared.identities.UserId;
import com.ccpp.shared.identities.WorkspaceId;
import com.ccpp.shared.valueobjects.DateRange;
import io.joffrey.ccpp.projectplanning.application.command.command.MarkProjectAsReadyCommand;
import io.joffrey.ccpp.projectplanning.domain.event.ProjectCreated;
import io.joffrey.ccpp.projectplanning.domain.event.ProjectMarkedAsReady;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class MarkProjectAsReadyHandlerTest {

    UUID commandId = UUID.randomUUID();
    UUID correlationId = UUID.randomUUID();
    EventBus eventBus = new SimpleEventBus();
    InMemoryEventStore eventStore = new InMemoryEventStore(eventBus);
    MarkProjectAsReadyHandler handler = new MarkProjectAsReadyHandler(eventStore);
    WorkspaceId workspaceId = new WorkspaceId(UUID.randomUUID());
    UserId userId = new UserId(UUID.randomUUID());
    ProjectId projectId = new ProjectId(UUID.randomUUID());
    DateRange timeline = new DateRange(LocalDate.of(2025, 1, 1), LocalDate.of(2025, 3, 31));
    String title = "Q1 Video Series";
    String description = "Educational content";
    BigDecimal projectBudgetLimit = BigDecimal.valueOf(1000);

    @Test
    void should_mark_project_as_ready() {
        eventStore.saveEvents(projectId.value(), List.of(new ProjectCreated(projectId, workspaceId, userId, title, description, timeline, projectBudgetLimit)), -1, null, null);

        handler.handle(new MarkProjectAsReadyCommand(
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
        var projectCreatedEvent = new ProjectCreated(projectId, workspaceId, userId, title, description, timeline, projectBudgetLimit);
        var projectMarkedAsReadyEvent = new ProjectMarkedAsReady(projectId, workspaceId, userId);
        eventStore.saveEvents(projectId.value(), List.of(projectCreatedEvent, projectMarkedAsReadyEvent), -1, null, null);

        var command = new MarkProjectAsReadyCommand(
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
