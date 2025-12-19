package io.joffrey.ccpp.projectplanning.application.command.handler;

import com.ccpp.shared.eventbus.EventBus;
import com.ccpp.shared.eventbus.SimpleEventBus;
import com.ccpp.shared.eventstore.InMemoryEventStore;
import com.ccpp.shared.identities.ProjectId;
import com.ccpp.shared.identities.UserId;
import com.ccpp.shared.identities.WorkspaceId;
import com.ccpp.shared.valueobjects.DateRange;
import io.joffrey.ccpp.projectplanning.application.command.command.ChangeTimelineCommand;
import io.joffrey.ccpp.projectplanning.domain.event.ProjectCreated;
import io.joffrey.ccpp.projectplanning.domain.event.ProjectMarkedAsReady;
import io.joffrey.ccpp.projectplanning.domain.event.ProjectTimelineChanged;
import io.joffrey.ccpp.projectplanning.domain.exception.CannotModifyReadyProjectException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ChangeTimelineHandlerTest {

    EventBus eventBus = new SimpleEventBus();
    InMemoryEventStore eventStore = new InMemoryEventStore(eventBus);
    ChangeTimelineHandler handler = new ChangeTimelineHandler(eventStore);

    WorkspaceId workspaceId = new WorkspaceId(UUID.randomUUID());
    UserId userId = new UserId(UUID.randomUUID());
    ProjectId projectId = new ProjectId(UUID.randomUUID());
    DateRange timeline = new DateRange(LocalDate.of(2025, 1, 1), LocalDate.of(2025, 3, 31));
    String title = "Q1 Video Series";
    String description = "Educational content";
    BigDecimal projectBudgetLimit = BigDecimal.valueOf(1000);

    UUID commandId = UUID.randomUUID();
    UUID correlationId = UUID.randomUUID();

    @Test
    void should_change_timeline_when_planning() {
        eventStore.saveEvents(projectId.value(), List.of(new ProjectCreated(projectId, workspaceId, userId, title, description, timeline, projectBudgetLimit)), -1, null, null);

        var newTimeline = new DateRange(LocalDate.of(2025, 2, 1), LocalDate.of(2025, 4, 30));

        handler.handle(new ChangeTimelineCommand(
                commandId,
                projectId,
                newTimeline,
                correlationId
        ));

        assertThat(eventStore.loadEvents(projectId.value()))
                .last()
                .isEqualTo(new ProjectTimelineChanged(projectId, newTimeline));
    }

    @Test
    void should_prevent_changing_timeline_when_ready() {
        eventStore.saveEvents(projectId.value(), List.of(
                new ProjectCreated(projectId, workspaceId, userId, title, description, timeline, projectBudgetLimit),
                new ProjectMarkedAsReady(projectId, workspaceId, userId)), -1, null, null);

        assertThatThrownBy(() -> handler.handle(
                new ChangeTimelineCommand(
                        commandId,
                        projectId,
                        new DateRange(LocalDate.of(2025, 2, 1), LocalDate.of(2025, 4, 30)),
                        correlationId
                )))
                .isInstanceOf(CannotModifyReadyProjectException.class)
                .hasMessageContaining("Cannot modify project in READY status");
    }
}
