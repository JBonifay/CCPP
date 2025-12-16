package io.joffrey.ccpp.projectplanning.application.command.handler;

import com.ccpp.shared.identities.ProjectId;
import com.ccpp.shared.identities.UserId;
import com.ccpp.shared.identities.WorkspaceId;
import com.ccpp.shared.valueobjects.DateRange;
import io.joffrey.ccpp.projectplanning.application.command.command.MarkProjectAsReadyCommand;
import io.joffrey.ccpp.projectplanning.domain.event.ProjectCreated;
import io.joffrey.ccpp.projectplanning.domain.event.ProjectMarkedAsReady;
import com.ccpp.shared.repository.InMemoryEventStore;
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
        eventStore.append(projectId.value(), List.of(new ProjectCreated(projectId, workspaceId, userId, title, description, timeline, projectBudgetLimit)), -1);

        handler.handle(new MarkProjectAsReadyCommand(projectId, userId));

        assertThat(eventStore.readStream(projectId.value()))
                .last()
                .isEqualTo(
                        new ProjectMarkedAsReady(projectId, workspaceId, userId)
                );
    }

    @Test
    void should_be_idempotent_when_marking_ready_project_as_ready() {
        var projectCreatedEvent = new ProjectCreated(projectId, workspaceId, userId, title, description, timeline, projectBudgetLimit);
        var projectMarkedAsReadyEvent = new ProjectMarkedAsReady(projectId, workspaceId, userId);
        eventStore.append(projectId.value(), List.of(projectCreatedEvent, projectMarkedAsReadyEvent), -1);

        var command = new MarkProjectAsReadyCommand(projectId, userId);

        handler.handle(command);

        assertThat(eventStore.readStream(projectId.value())).containsExactly(
                projectCreatedEvent,
                projectMarkedAsReadyEvent
        );
    }

}
