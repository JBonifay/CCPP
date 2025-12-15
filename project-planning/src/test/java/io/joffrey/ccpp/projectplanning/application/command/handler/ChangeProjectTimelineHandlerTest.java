package io.joffrey.ccpp.projectplanning.application.command.handler;

import com.ccpp.shared.identities.ProjectId;
import com.ccpp.shared.identities.UserId;
import com.ccpp.shared.identities.WorkspaceId;
import com.ccpp.shared.valueobjects.DateRange;
import io.joffrey.ccpp.projectplanning.application.command.command.ChangeProjectTimelineCommand;
import com.ccpp.shared.domain.event.ProjectCreated;
import io.joffrey.ccpp.projectplanning.domain.event.ProjectMarkedAsReady;
import io.joffrey.ccpp.projectplanning.domain.event.ProjectTimelineChanged;
import io.joffrey.ccpp.projectplanning.domain.exception.CannotModifyReadyProjectException;
import com.ccpp.shared.repository.InMemoryEventStore;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ChangeProjectTimelineHandlerTest {

    InMemoryEventStore eventStore = new InMemoryEventStore();
    ChangeProjectTimelineHandler handler = new ChangeProjectTimelineHandler(eventStore);

    WorkspaceId workspaceId = new WorkspaceId(UUID.randomUUID());
    UserId userId = new UserId(UUID.randomUUID());
    ProjectId projectId = new ProjectId(UUID.randomUUID());
    DateRange timeline = new DateRange(LocalDate.of(2025, 1, 1), LocalDate.of(2025, 3, 31));
    String title = "Q1 Video Series";
    String description = "Educational content";
    BigDecimal projectBudgetLimit = BigDecimal.valueOf(1000);

    @Test
    void should_change_timeline_when_planning() {
        var projectCreatedEvent = new ProjectCreated(projectId, workspaceId, userId, title, description, timeline, projectBudgetLimit);
        eventStore.append(projectId.value(), List.of(projectCreatedEvent), -1);

        var newTimeline = new DateRange(LocalDate.of(2025, 2, 1), LocalDate.of(2025, 4, 30));
        var command = new ChangeProjectTimelineCommand(projectId, newTimeline);

        handler.handle(command);

        assertThat(eventStore.readStream(projectId.value()))
                .last()
                .isEqualTo(new ProjectTimelineChanged(projectId, newTimeline));
    }

    @Test
    void should_prevent_changing_timeline_when_ready() {
        eventStore.append(projectId.value(), List.of(
                        new ProjectCreated(projectId, workspaceId, userId, title, description, timeline, projectBudgetLimit),
                        new ProjectMarkedAsReady(projectId, workspaceId, userId)), -1);

        assertThatThrownBy(() -> handler.handle(new ChangeProjectTimelineCommand(projectId, new DateRange(LocalDate.of(2025, 2, 1), LocalDate.of(2025, 4, 30)))))
                .isInstanceOf(CannotModifyReadyProjectException.class)
                .hasMessageContaining("Cannot modify project in READY status");
    }
}
