package io.joffrey.ccpp.projectplanning.application.command.handler;

import com.ccpp.shared.eventbus.EventBus;
import com.ccpp.shared.eventbus.SimpleEventBus;
import com.ccpp.shared.eventstore.InMemoryEventStore;
import com.ccpp.shared.identities.ProjectId;
import com.ccpp.shared.identities.UserId;
import com.ccpp.shared.identities.WorkspaceId;
import com.ccpp.shared.valueobjects.DateRange;
import io.joffrey.ccpp.projectplanning.application.command.command.AddNoteCommand;
import io.joffrey.ccpp.projectplanning.domain.event.NoteAdded;
import io.joffrey.ccpp.projectplanning.domain.event.ProjectCreated;
import io.joffrey.ccpp.projectplanning.domain.exception.InvalidProjectNoteException;
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
        eventStore.saveEvents(projectId.value(), List.of(new ProjectCreated(projectId, workspaceId, userId, title, description, timeline, projectBudgetLimit)), -1, null, null);

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
        eventStore.saveEvents(projectId.value(), List.of(new ProjectCreated(projectId, workspaceId, userId, title, description, timeline, projectBudgetLimit)), -1, null, null);

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
        eventStore.saveEvents(projectId.value(), List.of(new ProjectCreated(projectId, workspaceId, userId, title, description, timeline, projectBudgetLimit)), -1, null, null);

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
