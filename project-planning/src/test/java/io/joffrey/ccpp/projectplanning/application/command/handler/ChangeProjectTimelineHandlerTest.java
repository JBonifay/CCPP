package io.joffrey.ccpp.projectplanning.application.command.handler;

import com.ccpp.shared.identities.ProjectId;
import com.ccpp.shared.identities.UserId;
import com.ccpp.shared.identities.WorkspaceId;
import com.ccpp.shared.valueobjects.DateRange;
import io.joffrey.ccpp.projectplanning.application.command.ChangeProjectTimelineCommand;
import io.joffrey.ccpp.projectplanning.domain.event.ProjectCreated;
import io.joffrey.ccpp.projectplanning.domain.event.ProjectMarkedAsReady;
import io.joffrey.ccpp.projectplanning.domain.event.ProjectTimelineChanged;
import io.joffrey.ccpp.projectplanning.domain.exception.CannotModifyReadyProjectException;
import io.joffrey.ccpp.projectplanning.infrastructure.event.InMemoryEventStore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ChangeProjectTimelineHandlerTest {

    private InMemoryEventStore eventStore;
    private ChangeProjectTimelineHandler handler;

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
        handler = new ChangeProjectTimelineHandler(eventStore);

        workspaceId = new WorkspaceId(UUID.randomUUID());
        userId = new UserId(UUID.randomUUID());
        projectId = new ProjectId(UUID.randomUUID());
        timeline = new DateRange(LocalDate.of(2025, 1, 1), LocalDate.of(2025, 3, 31));
        title = "Q1 Video Series";
        description = "Educational content";
        projectBudgetLimit = BigDecimal.valueOf(1000);
    }

    @Test
    void should_change_timeline_when_planning() {
        // GIVEN - project exists and is in PLANNING status
        var projectCreatedEvent = new ProjectCreated(
                workspaceId,
                userId,
                projectId,
                title,
                description,
                timeline,
                projectBudgetLimit
        );
        eventStore.append(projectId.value(), java.util.List.of(projectCreatedEvent), -1);

        var newTimeline = new DateRange(LocalDate.of(2025, 2, 1), LocalDate.of(2025, 4, 30));
        var command = new ChangeProjectTimelineCommand(projectId, newTimeline);

        // WHEN
        handler.handle(command);

        // THEN
        var events = eventStore.readStream(projectId.value());
        assertThat(events).hasSize(2);
        assertThat(events.get(1)).isInstanceOf(ProjectTimelineChanged.class);

        var timelineChangedEvent = (ProjectTimelineChanged) events.get(1);
        assertThat(timelineChangedEvent.projectId()).isEqualTo(projectId);
        assertThat(timelineChangedEvent.newTimeline()).isEqualTo(newTimeline);
    }

    @Test
    void should_prevent_changing_timeline_when_ready() {
        // GIVEN - project is marked as READY
        var projectCreatedEvent = new ProjectCreated(
                workspaceId,
                userId,
                projectId,
                title,
                description,
                timeline,
                projectBudgetLimit
        );
        var projectMarkedAsReadyEvent = new ProjectMarkedAsReady(projectId, workspaceId, userId);

        eventStore.append(
                projectId.value(),
                java.util.List.of(projectCreatedEvent, projectMarkedAsReadyEvent),
                -1
        );

        var newTimeline = new DateRange(LocalDate.of(2025, 2, 1), LocalDate.of(2025, 4, 30));
        var command = new ChangeProjectTimelineCommand(projectId, newTimeline);

        // WHEN & THEN
        assertThatThrownBy(() -> handler.handle(command))
                .isInstanceOf(CannotModifyReadyProjectException.class)
                .hasMessageContaining("Cannot modify project in READY status");

        // Verify no new events were persisted
        var events = eventStore.readStream(projectId.value());
        assertThat(events).hasSize(2);  // Only ProjectCreated + ProjectMarkedAsReady
    }
}
