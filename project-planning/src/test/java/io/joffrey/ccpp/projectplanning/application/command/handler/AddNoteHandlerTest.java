package io.joffrey.ccpp.projectplanning.application.command.handler;

import com.ccpp.shared.identities.ProjectId;
import com.ccpp.shared.identities.UserId;
import com.ccpp.shared.identities.WorkspaceId;
import com.ccpp.shared.valueobjects.DateRange;
import io.joffrey.ccpp.projectplanning.application.command.AddNoteCommand;
import io.joffrey.ccpp.projectplanning.domain.event.NoteAdded;
import io.joffrey.ccpp.projectplanning.domain.event.ProjectCreated;
import io.joffrey.ccpp.projectplanning.domain.exception.InvalidProjectNoteException;
import io.joffrey.ccpp.projectplanning.infrastructure.event.InMemoryEventStore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AddNoteHandlerTest {

    private InMemoryEventStore eventStore;
    private AddNoteHandler handler;

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
        handler = new AddNoteHandler(eventStore);

        workspaceId = new WorkspaceId(UUID.randomUUID());
        userId = new UserId(UUID.randomUUID());
        projectId = new ProjectId(UUID.randomUUID());
        timeline = new DateRange(LocalDate.of(2025, 1, 1), LocalDate.of(2025, 3, 31));
        title = "Q1 Video Series";
        description = "Educational content";
        projectBudgetLimit = BigDecimal.valueOf(1000);
    }

    @Test
    void should_add_note_to_project() {
        // GIVEN - project exists
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

        var command = new AddNoteCommand(projectId, "Need to book studio for recording", userId);

        // WHEN
        handler.handle(command);

        // THEN
        assertThat(eventStore.readStream(projectId.value()))
                .hasSize(2)
                .satisfies(events -> {
                    assertThat(events.get(1)).isInstanceOf(NoteAdded.class);
                    var noteAddedEvent = (NoteAdded) events.get(1);
                    assertThat(noteAddedEvent.getProjectId()).isEqualTo(projectId);
                    assertThat(noteAddedEvent.getContent()).isEqualTo("Need to book studio for recording");
                    assertThat(noteAddedEvent.getUserId()).isEqualTo(userId);
                });
    }

    @Test
    void should_reject_empty_note_content() {
        // GIVEN - project exists
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

        var command = new AddNoteCommand(projectId, "", userId);  // empty content

        // WHEN & THEN
        assertThatThrownBy(() -> handler.handle(command))
                .isInstanceOf(InvalidProjectNoteException.class)
                .hasMessageContaining("Note content cannot be empty");

        assertThat(eventStore.readStream(projectId.value())).hasSize(1);
    }

    @Test
    void should_reject_null_note_content() {
        // GIVEN - project exists
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

        var command = new AddNoteCommand(projectId, null, userId);  // null content

        // WHEN & THEN
        assertThatThrownBy(() -> handler.handle(command))
                .isInstanceOf(InvalidProjectNoteException.class)
                .hasMessageContaining("Note content cannot be empty");

        assertThat(eventStore.readStream(projectId.value())).hasSize(1);
    }
}
