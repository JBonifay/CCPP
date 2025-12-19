package io.joffrey.ccpp.projectplanning.application.command.handler;

import com.ccpp.shared.eventbus.EventBus;
import com.ccpp.shared.eventbus.SimpleEventBus;
import com.ccpp.shared.eventstore.InMemoryEventStore;
import com.ccpp.shared.exception.DateRangeException;
import com.ccpp.shared.identities.ProjectId;
import com.ccpp.shared.identities.UserId;
import com.ccpp.shared.identities.WorkspaceId;
import com.ccpp.shared.valueobjects.DateRange;
import io.joffrey.ccpp.projectplanning.application.command.command.RequestProjectCreationCommand;
import io.joffrey.ccpp.projectplanning.domain.event.ProjectCreated;
import io.joffrey.ccpp.projectplanning.domain.exception.InvalidProjectDataException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RequestProjectCreationHandlerTest {

    EventBus eventBus = new SimpleEventBus();
    InMemoryEventStore eventStore = new InMemoryEventStore(eventBus);
    RequestProjectCreationHandler handler = new RequestProjectCreationHandler(eventStore);

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
    void should_create_project_with_valid_data() {
        handler.handle(new RequestProjectCreationCommand(
                commandId,
                workspaceId,
                userId,
                projectId,
                title,
                description,
                timeline,
                projectBudgetLimit,
                correlationId
        ));

        assertThat(eventStore.loadEvents(projectId.value())).containsExactly(
                new ProjectCreated(projectId, workspaceId, userId, title, description, timeline, projectBudgetLimit)
        );
    }

    @Test
    void should_reject_project_with_invalid_timeline() {
        assertThatThrownBy(() -> new RequestProjectCreationCommand(
                commandId,
                workspaceId,
                userId,
                projectId,
                title,
                description,
                new DateRange(LocalDate.of(2025, 3, 31), LocalDate.of(2025, 1, 1)),
                projectBudgetLimit,
                correlationId
        ))
                .isInstanceOf(DateRangeException.class)
                .hasMessageContaining("Start date must be before end date");
    }

    @Test
    void should_reject_project_with_empty_title() {
        var command = new RequestProjectCreationCommand(
                commandId,
                workspaceId,
                userId,
                projectId,
                "",  // empty title
                description,
                timeline,
                projectBudgetLimit,
                correlationId
        );

        assertThatThrownBy(() -> handler.handle(command))
                .isInstanceOf(InvalidProjectDataException.class)
                .hasMessageContaining("Title cannot be empty");
    }

    @Test
    void should_reject_project_with_null_title() {
        var command = new RequestProjectCreationCommand(
                commandId,
                workspaceId,
                userId,
                projectId,
                null,  // null title
                description,
                timeline,
                projectBudgetLimit,
                correlationId
        );

        assertThatThrownBy(() -> handler.handle(command))
                .isInstanceOf(InvalidProjectDataException.class)
                .hasMessageContaining("Title cannot be empty");
    }

    @Test
    void should_reject_project_with_empty_description() {
        var command = new RequestProjectCreationCommand(
                commandId,
                workspaceId,
                userId,
                projectId,
                title,
                "",  // empty description
                timeline,
                projectBudgetLimit,
                correlationId
        );

        assertThatThrownBy(() -> handler.handle(command))
                .isInstanceOf(InvalidProjectDataException.class)
                .hasMessageContaining("Description cannot be empty");
    }

    @Test
    void should_reject_project_with_null_description() {
        var command = new RequestProjectCreationCommand(
                commandId,
                workspaceId,
                userId,
                projectId,
                title,
                null,  // null description
                timeline,
                projectBudgetLimit,
                correlationId
        );

        assertThatThrownBy(() -> handler.handle(command))
                .isInstanceOf(InvalidProjectDataException.class)
                .hasMessageContaining("Description cannot be empty");
    }
}
