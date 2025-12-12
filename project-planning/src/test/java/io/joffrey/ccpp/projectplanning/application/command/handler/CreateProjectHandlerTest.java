package io.joffrey.ccpp.projectplanning.application.command.handler;

import com.ccpp.shared.exception.DateRangeException;
import com.ccpp.shared.identities.ProjectId;
import com.ccpp.shared.identities.UserId;
import com.ccpp.shared.identities.WorkspaceId;
import com.ccpp.shared.valueobjects.DateRange;
import io.joffrey.ccpp.projectplanning.application.command.CreateProjectCommand;
import io.joffrey.ccpp.projectplanning.domain.event.ProjectCreated;
import io.joffrey.ccpp.projectplanning.domain.exception.InvalidProjectDataException;
import com.ccpp.shared.repository.InMemoryEventStore;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CreateProjectHandlerTest {

    InMemoryEventStore eventStore = new InMemoryEventStore();
    CreateProjectHandler handler = new CreateProjectHandler(eventStore);

    WorkspaceId workspaceId = new WorkspaceId(UUID.randomUUID());
    UserId userId = new UserId(UUID.randomUUID());
    ProjectId projectId = new ProjectId(UUID.randomUUID());
    DateRange timeline = new DateRange(LocalDate.of(2025, 1, 1), LocalDate.of(2025, 3, 31));
    String title = "Q1 Video Series";
    String description = "Educational content";
    BigDecimal projectBudgetLimit = BigDecimal.valueOf(1000);

    @Test
    void should_create_project_with_valid_data() {
        var command = new CreateProjectCommand(
                workspaceId,
                userId,
                projectId,
                title,
                description,
                timeline,
                projectBudgetLimit
        );

        handler.handle(command);

        assertThat(eventStore.readStream(projectId.value())).containsExactly(
                        new ProjectCreated(
                                projectId,
                                workspaceId,
                                userId,
                                title,
                                description,
                                timeline,
                                projectBudgetLimit
                        )
                );
    }

    @Test
    void should_reject_project_with_invalid_timeline() {
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
        var command = new CreateProjectCommand(
                workspaceId,
                userId,
                projectId,
                "",  // empty title
                description,
                timeline,
                projectBudgetLimit
        );

        assertThatThrownBy(() -> handler.handle(command))
                .isInstanceOf(InvalidProjectDataException.class)
                .hasMessageContaining("Title cannot be empty");
    }

    @Test
    void should_reject_project_with_null_title() {
        var command = new CreateProjectCommand(
                workspaceId,
                userId,
                projectId,
                null,  // null title
                description,
                timeline,
                projectBudgetLimit
        );

        assertThatThrownBy(() -> handler.handle(command))
                .isInstanceOf(InvalidProjectDataException.class)
                .hasMessageContaining("Title cannot be empty");
    }

    @Test
    void should_reject_project_with_empty_description() {
        var command = new CreateProjectCommand(
                workspaceId,
                userId,
                projectId,
                title,
                "",  // empty description
                timeline,
                projectBudgetLimit
        );

        assertThatThrownBy(() -> handler.handle(command))
                .isInstanceOf(InvalidProjectDataException.class)
                .hasMessageContaining("Description cannot be empty");
    }

    @Test
    void should_reject_project_with_null_description() {
        var command = new CreateProjectCommand(
                workspaceId,
                userId,
                projectId,
                title,
                null,  // null description
                timeline,
                projectBudgetLimit
        );

        assertThatThrownBy(() -> handler.handle(command))
                .isInstanceOf(InvalidProjectDataException.class)
                .hasMessageContaining("Description cannot be empty");
    }
}
