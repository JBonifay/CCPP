package fr.joffreybonifay.ccpp.projectplanning.application.command.handler;

import fr.joffreybonifay.ccpp.shared.event.ProjectCreationRequested;
import fr.joffreybonifay.ccpp.shared.eventbus.EventBus;
import fr.joffreybonifay.ccpp.shared.eventbus.SimpleEventBus;
import fr.joffreybonifay.ccpp.shared.eventstore.InMemoryEventStore;
import fr.joffreybonifay.ccpp.shared.identities.ProjectId;
import fr.joffreybonifay.ccpp.shared.identities.UserId;
import fr.joffreybonifay.ccpp.shared.identities.WorkspaceId;
import fr.joffreybonifay.ccpp.shared.valueobjects.DateRange;
import fr.joffreybonifay.ccpp.projectplanning.application.command.command.ChangeTimelineCommand;
import fr.joffreybonifay.ccpp.projectplanning.domain.event.ProjectMarkedAsReady;
import fr.joffreybonifay.ccpp.projectplanning.domain.event.ProjectTimelineChanged;
import fr.joffreybonifay.ccpp.projectplanning.domain.exception.CannotModifyReadyProjectException;
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
        eventStore.saveEvents(projectId.value(), List.of(new ProjectCreationRequested(projectId, workspaceId, userId, title, description, timeline, projectBudgetLimit)), -1, null, null);

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
                new ProjectCreationRequested(projectId, workspaceId, userId, title, description, timeline, projectBudgetLimit),
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
