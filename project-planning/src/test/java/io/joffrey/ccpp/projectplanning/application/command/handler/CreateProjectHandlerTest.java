package io.joffrey.ccpp.projectplanning.application.command.handler;

import com.ccpp.shared.exception.DateRangeException;
import com.ccpp.shared.identities.ProjectId;
import com.ccpp.shared.identities.UserId;
import com.ccpp.shared.identities.WorkspaceId;
import com.ccpp.shared.valueobjects.DateRange;
import io.joffrey.ccpp.projectplanning.application.command.CreateProjectCommand;
import io.joffrey.ccpp.projectplanning.domain.event.ProjectCreated;
import io.joffrey.ccpp.projectplanning.domain.exception.InvalidProjectDataException;
import io.joffrey.ccpp.projectplanning.infrastructure.event.InMemoryEventStore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CreateProjectHandlerTest {

    private InMemoryEventStore eventStore;
    private CreateProjectHandler handler;

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
        handler = new CreateProjectHandler(eventStore);

        workspaceId = new WorkspaceId(UUID.randomUUID());
        userId = new UserId(UUID.randomUUID());
        projectId = new ProjectId(UUID.randomUUID());
        timeline = new DateRange(LocalDate.of(2025, 1, 1), LocalDate.of(2025, 3, 31));
        title = "Q1 Video Series";
        description = "Educational content";
        projectBudgetLimit = BigDecimal.valueOf(1000);
    }

    @Test
    void should_create_project_with_valid_data() {
        // GIVEN
        var command = new CreateProjectCommand(
                workspaceId,
                userId,
                projectId,
                title,
                description,
                timeline,
                projectBudgetLimit
        );

        // WHEN
        handler.handle(command);

        // THEN
        var events = eventStore.readStream(projectId.value().toString());
        assertThat(events).hasSize(1);
        assertThat(events.get(0)).isInstanceOf(ProjectCreated.class);

        var event = (ProjectCreated) events.get(0);
        assertThat(event.projectId()).isEqualTo(projectId);
        assertThat(event.workspaceId()).isEqualTo(workspaceId);
        assertThat(event.userId()).isEqualTo(userId);
        assertThat(event.title()).isEqualTo(title);
        assertThat(event.description()).isEqualTo(description);
        assertThat(event.timeline()).isEqualTo(timeline);
        assertThat(event.projectBudgetLimit()).isEqualTo(projectBudgetLimit);
    }

    @Test
    void should_reject_project_with_invalid_timeline() {
        // GIVEN & WHEN & THEN - invalid timeline (end before start)
        // DateRange validates in constructor, so exception is thrown when creating command
        assertThatThrownBy(() -> new CreateProjectCommand(
                workspaceId,
                userId,
                projectId,
                title,
                description,
                new DateRange(LocalDate.of(2025, 3, 31), LocalDate.of(2025, 1, 1)),
                projectBudgetLimit
        ))
                .isInstanceOf(DateRangeException.class)
                .hasMessageContaining("Start date must be before end date");
    }

    @Test
    void should_reject_project_with_empty_title() {
        // GIVEN
        var command = new CreateProjectCommand(
                workspaceId,
                userId,
                projectId,
                "",  // empty title
                description,
                timeline,
                projectBudgetLimit
        );

        // WHEN & THEN
        assertThatThrownBy(() -> handler.handle(command))
                .isInstanceOf(InvalidProjectDataException.class)
                .hasMessageContaining("Title cannot be empty");

        var events = eventStore.readStream(projectId.value().toString());
        assertThat(events).isEmpty();
    }

    @Test
    void should_reject_project_with_null_title() {
        // GIVEN
        var command = new CreateProjectCommand(
                workspaceId,
                userId,
                projectId,
                null,  // null title
                description,
                timeline,
                projectBudgetLimit
        );

        // WHEN & THEN
        assertThatThrownBy(() -> handler.handle(command))
                .isInstanceOf(InvalidProjectDataException.class)
                .hasMessageContaining("Title cannot be empty");

        var events = eventStore.readStream(projectId.value().toString());
        assertThat(events).isEmpty();
    }

    @Test
    void should_reject_project_with_empty_description() {
        // GIVEN
        var command = new CreateProjectCommand(
                workspaceId,
                userId,
                projectId,
                title,
                "",  // empty description
                timeline,
                projectBudgetLimit
        );

        // WHEN & THEN
        assertThatThrownBy(() -> handler.handle(command))
                .isInstanceOf(InvalidProjectDataException.class)
                .hasMessageContaining("Description cannot be empty");

        var events = eventStore.readStream(projectId.value().toString());
        assertThat(events).isEmpty();
    }

    @Test
    void should_reject_project_with_null_description() {
        // GIVEN
        var command = new CreateProjectCommand(
                workspaceId,
                userId,
                projectId,
                title,
                null,  // null description
                timeline,
                projectBudgetLimit
        );

        // WHEN & THEN
        assertThatThrownBy(() -> handler.handle(command))
                .isInstanceOf(InvalidProjectDataException.class)
                .hasMessageContaining("Description cannot be empty");

        var events = eventStore.readStream(projectId.value().toString());
        assertThat(events).isEmpty();
    }
}
