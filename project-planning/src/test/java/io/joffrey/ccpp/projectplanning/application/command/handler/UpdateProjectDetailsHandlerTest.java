package io.joffrey.ccpp.projectplanning.application.command.handler;

import com.ccpp.shared.identities.ProjectId;
import com.ccpp.shared.identities.UserId;
import com.ccpp.shared.identities.WorkspaceId;
import com.ccpp.shared.valueobjects.DateRange;
import io.joffrey.ccpp.projectplanning.application.command.UpdateProjectDetailsCommand;
import io.joffrey.ccpp.projectplanning.domain.event.ProjectCreated;
import io.joffrey.ccpp.projectplanning.domain.event.ProjectDetailsUpdated;
import io.joffrey.ccpp.projectplanning.domain.exception.InvalidProjectDataException;
import io.joffrey.ccpp.projectplanning.infrastructure.event.InMemoryEventStore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UpdateProjectDetailsHandlerTest {

    private InMemoryEventStore eventStore;
    private UpdateProjectDetailsHandler handler;

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
        handler = new UpdateProjectDetailsHandler(eventStore);

        workspaceId = new WorkspaceId(UUID.randomUUID());
        userId = new UserId(UUID.randomUUID());
        projectId = new ProjectId(UUID.randomUUID());
        timeline = new DateRange(LocalDate.of(2025, 1, 1), LocalDate.of(2025, 3, 31));
        title = "Q1 Video Series";
        description = "Educational content";
        projectBudgetLimit = BigDecimal.valueOf(1000);
    }

    @Test
    void should_update_project_details() {
        var projectCreatedEvent = new ProjectCreated(
                workspaceId,
                userId,
                projectId,
                title,
                description,
                timeline,
                projectBudgetLimit
        );
        eventStore.append(projectId.value(), List.of(projectCreatedEvent), -1);

        var command = new UpdateProjectDetailsCommand(projectId, "Q2 Video Series", "Updated educational content");

        handler.handle(command);

        assertThat(eventStore.readStream(projectId.value())).containsExactly(
                new ProjectDetailsUpdated(projectId, "Q2 Video Series", "Updated educational content")
        );
    }

    @Test
    void should_reject_empty_title_on_update() {
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
        eventStore.append(projectId.value(), List.of(projectCreatedEvent), -1);

        var command = new UpdateProjectDetailsCommand(
                projectId,
                "",  // empty title
                "Valid description"
        );

        // WHEN & THEN
        assertThatThrownBy(() -> handler.handle(command))
                .isInstanceOf(InvalidProjectDataException.class)
                .hasMessageContaining("Title cannot be empty");

        // Verify no new events were persisted
        assertThat(eventStore.readStream(projectId.value())).hasSize(1);  // Only ProjectCreated
    }

    @Test
    void should_reject_empty_description_on_update() {
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
        eventStore.append(projectId.value(), List.of(projectCreatedEvent), -1);

        var command = new UpdateProjectDetailsCommand(
                projectId,
                "Valid title",
                ""  // empty description
        );

        // WHEN & THEN
        assertThatThrownBy(() -> handler.handle(command))
                .isInstanceOf(InvalidProjectDataException.class)
                .hasMessageContaining("Description cannot be empty");

        assertThat(eventStore.readStream(projectId.value())).hasSize(1);
    }

    @Test
    void should_reject_null_title_on_update() {
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
        eventStore.append(projectId.value(), List.of(projectCreatedEvent), -1);

        var command = new UpdateProjectDetailsCommand(
                projectId,
                null,  // null title
                "Valid description"
        );

        // WHEN & THEN
        assertThatThrownBy(() -> handler.handle(command))
                .isInstanceOf(InvalidProjectDataException.class)
                .hasMessageContaining("Title cannot be empty");

        assertThat(eventStore.readStream(projectId.value())).hasSize(1);
    }

    @Test
    void should_reject_null_description_on_update() {
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
        eventStore.append(projectId.value(), List.of(projectCreatedEvent), -1);

        var command = new UpdateProjectDetailsCommand(
                projectId,
                "Valid title",
                null  // null description
        );

        // WHEN & THEN
        assertThatThrownBy(() -> handler.handle(command))
                .isInstanceOf(InvalidProjectDataException.class)
                .hasMessageContaining("Description cannot be empty");

        assertThat(eventStore.readStream(projectId.value())).hasSize(1);
    }
}
