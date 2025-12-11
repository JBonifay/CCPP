package io.joffrey.ccpp.projectplanning.application.command.handler;

import com.ccpp.shared.identities.ProjectId;
import com.ccpp.shared.identities.UserId;
import com.ccpp.shared.identities.WorkspaceId;
import com.ccpp.shared.valueobjects.DateRange;
import io.joffrey.ccpp.projectplanning.application.command.MarkProjectAsReadyCommand;
import io.joffrey.ccpp.projectplanning.domain.event.ProjectCreated;
import io.joffrey.ccpp.projectplanning.domain.event.ProjectMarkedAsReady;
import io.joffrey.ccpp.projectplanning.infrastructure.event.InMemoryEventStore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class MarkProjectAsReadyHandlerTest {

    private InMemoryEventStore eventStore;
    private MarkProjectAsReadyHandler handler;

    private WorkspaceId workspaceId;
    private UserId userId;
    private ProjectId projectId;
    private DateRange timeline;
    private String title;
    private String description;
    private BigDecimal projectBudgetLimit;

    @BeforeEach
    void setUp() {
        eventStore = new InMemoryEventStore();
        handler = new MarkProjectAsReadyHandler(eventStore);

        workspaceId = new WorkspaceId(UUID.randomUUID());
        userId = new UserId(UUID.randomUUID());
        projectId = new ProjectId(UUID.randomUUID());
        timeline = new DateRange(LocalDate.of(2025, 1, 1), LocalDate.of(2025, 3, 31));
        title = "Q1 Video Series";
        description = "Educational content";
        projectBudgetLimit = BigDecimal.valueOf(1000);
    }

    @Test
    void should_mark_project_as_ready() {
        // GIVEN - project exists (ProjectCreated event in event store)
        var projectCreatedEvent = new ProjectCreated(
                projectId,
                workspaceId,
                userId,
                title,
                description,
                timeline,
                projectBudgetLimit
        );
        eventStore.append(projectId.value(), List.of(projectCreatedEvent), -1);

        var command = new MarkProjectAsReadyCommand(projectId, userId);

        // WHEN
        handler.handle(command);

        // THEN
        assertThat(eventStore.readStream(projectId.value()))
                .hasSize(2)  // ProjectCreated + ProjectMarkedAsReady
                .satisfies(events -> {
                    assertThat(events.get(1)).isInstanceOf(ProjectMarkedAsReady.class);
                    var markedAsReadyEvent = (ProjectMarkedAsReady) events.get(1);
                    assertThat(markedAsReadyEvent.projectId()).isEqualTo(projectId);
                    assertThat(markedAsReadyEvent.workspaceId()).isEqualTo(workspaceId);
                    assertThat(markedAsReadyEvent.userId()).isEqualTo(userId);
                });
    }

    @Test
    void should_be_idempotent_when_marking_ready_project_as_ready() {
        // GIVEN - project is already marked as ready (ProjectCreated + ProjectMarkedAsReady in event store)
        var projectCreatedEvent = new ProjectCreated(
                projectId,
                workspaceId,
                userId,
                title,
                description,
                timeline,
                projectBudgetLimit
        );
        var projectMarkedAsReadyEvent = new ProjectMarkedAsReady(projectId, workspaceId, userId);

        eventStore.append(
                projectId.value(),
                List.of(projectCreatedEvent, projectMarkedAsReadyEvent),
                -1
        );

        var command = new MarkProjectAsReadyCommand(projectId, userId);

        // WHEN
        handler.handle(command);

        // THEN - no new event is added (idempotent behavior)
        assertThat(eventStore.readStream(projectId.value()))
                .hasSize(2);  // Still only ProjectCreated + ProjectMarkedAsReady (no duplicate)
    }
}
