package fr.joffreybonifay.ccpp.projectplanning.application.command.handler;

import fr.joffreybonifay.ccpp.projectplanning.application.command.command.AddNoteCommand;
import fr.joffreybonifay.ccpp.projectplanning.domain.event.NoteAdded;
import fr.joffreybonifay.ccpp.projectplanning.domain.exception.InvalidProjectNoteException;
import fr.joffreybonifay.ccpp.shared.event.ProjectCreationRequested;
import fr.joffreybonifay.ccpp.shared.eventbus.EventBus;
import fr.joffreybonifay.ccpp.shared.eventbus.SimpleEventBus;
import fr.joffreybonifay.ccpp.shared.eventstore.AggregateType;
import fr.joffreybonifay.ccpp.shared.eventstore.EventMetadata;
import fr.joffreybonifay.ccpp.shared.eventstore.impl.InMemoryEventStore;
import fr.joffreybonifay.ccpp.shared.identities.ProjectId;
import fr.joffreybonifay.ccpp.shared.identities.UserId;
import fr.joffreybonifay.ccpp.shared.identities.WorkspaceId;
import fr.joffreybonifay.ccpp.shared.valueobjects.DateRange;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AddNoteHandlerTest {

    EventBus eventBus = new SimpleEventBus();
    InMemoryEventStore eventStore = new InMemoryEventStore(eventBus);
    AddNoteHandler handler = new AddNoteHandler(eventStore);

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
    void should_add_note_to_project() {
        eventStore.saveEvents(projectId.value(),
                AggregateType.PROJECT_PLANNING,
                List.of(
                        new EventMetadata(new ProjectCreationRequested(projectId, workspaceId, userId, title, description, timeline, projectBudgetLimit), null, null, null)),
                -1);

        handler.handle(new AddNoteCommand(
                commandId,
                projectId,
                "Need to book studio for recording",
                userId,
                correlationId
        ));

        assertThat(eventStore.loadEvents(projectId.value()))
                .last()
                .isEqualTo(new NoteAdded(projectId, "Need to book studio for recording", userId));
    }

    @Test
    void should_reject_empty_note_content() {
        eventStore.saveEvents(projectId.value(),
                AggregateType.PROJECT_PLANNING,
                List.of(new EventMetadata(new ProjectCreationRequested(projectId, workspaceId, userId, title, description, timeline, projectBudgetLimit), null, null, null)),
                -1);

        assertThatThrownBy(() -> handler.handle(
                new AddNoteCommand(
                        commandId,
                        projectId, "", userId,
                        correlationId
                )))
                .isInstanceOf(InvalidProjectNoteException.class)
                .hasMessageContaining("Note content cannot be empty");
    }

    @Test
    void should_reject_null_note_content() {
        eventStore.saveEvents(projectId.value(),
                AggregateType.PROJECT_PLANNING,
                List.of(new EventMetadata(new ProjectCreationRequested(projectId, workspaceId, userId, title, description, timeline, projectBudgetLimit), null, null, null)),
                -1);

        assertThatThrownBy(() -> handler.handle(
                new AddNoteCommand(
                        commandId,
                        projectId,
                        null,
                        userId,
                        correlationId)))
                .isInstanceOf(InvalidProjectNoteException.class)
                .hasMessageContaining("Note content cannot be empty");
    }
}
