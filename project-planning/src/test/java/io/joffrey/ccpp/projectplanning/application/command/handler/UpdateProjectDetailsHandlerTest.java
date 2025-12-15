package io.joffrey.ccpp.projectplanning.application.command.handler;

import com.ccpp.shared.identities.ProjectId;
import com.ccpp.shared.identities.UserId;
import com.ccpp.shared.identities.WorkspaceId;
import com.ccpp.shared.valueobjects.DateRange;
import io.joffrey.ccpp.projectplanning.application.command.command.UpdateProjectDetailsCommand;
import com.ccpp.shared.domain.event.ProjectCreated;
import io.joffrey.ccpp.projectplanning.domain.event.ProjectDetailsUpdated;
import io.joffrey.ccpp.projectplanning.domain.exception.InvalidProjectDataException;
import com.ccpp.shared.repository.InMemoryEventStore;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UpdateProjectDetailsHandlerTest {

    InMemoryEventStore eventStore = new InMemoryEventStore();
    UpdateProjectDetailsHandler handler = new UpdateProjectDetailsHandler(eventStore);

    WorkspaceId workspaceId = new WorkspaceId(UUID.randomUUID());
    UserId userId = new UserId(UUID.randomUUID());
    ProjectId projectId = new ProjectId(UUID.randomUUID());
    DateRange timeline = new DateRange(LocalDate.of(2025, 1, 1), LocalDate.of(2025, 3, 31));
    String title = "Q1 Video Series";
    String description = "Educational content";
    BigDecimal projectBudgetLimit = BigDecimal.valueOf(1000);

    @Test
    void should_update_project_details() {
        eventStore.append(projectId.value(), List.of(new ProjectCreated(projectId, workspaceId, userId, title, description, timeline, projectBudgetLimit)), -1);

        handler.handle(new UpdateProjectDetailsCommand(projectId, "Q2 Video Series", "Updated educational content"));

        assertThat(eventStore.readStream(projectId.value()))
                .last()
                .isEqualTo(new ProjectDetailsUpdated(projectId, "Q2 Video Series", "Updated educational content"));
    }

    @Test
    void should_reject_empty_title_on_update() {
        eventStore.append(projectId.value(), List.of(new ProjectCreated(projectId, workspaceId, userId, title, description, timeline, projectBudgetLimit)), -1);

        assertThatThrownBy(() -> handler.handle(
                new UpdateProjectDetailsCommand(
                        projectId,
                        "",  // empty title
                        "Valid description")
        )).isInstanceOf(InvalidProjectDataException.class)
                .hasMessageContaining("Title cannot be empty");
    }

    @Test
    void should_reject_empty_description_on_update() {
        eventStore.append(projectId.value(), List.of(new ProjectCreated(projectId, workspaceId, userId, title, description, timeline, projectBudgetLimit)), -1);

        assertThatThrownBy(() -> handler.handle(new UpdateProjectDetailsCommand(
                projectId,
                "Valid title",
                ""  // empty description
        )))
                .isInstanceOf(InvalidProjectDataException.class)
                .hasMessageContaining("Description cannot be empty");
    }

    @Test
    void should_reject_null_title_on_update() {
        ProjectCreated projectCreatedEvent = new ProjectCreated(projectId, workspaceId, userId, title, description, timeline, projectBudgetLimit);
        eventStore.append(projectId.value(), List.of(projectCreatedEvent), -1);

        assertThatThrownBy(() -> handler.handle(new UpdateProjectDetailsCommand(
                projectId,
                null,  // null title
                "Valid description"
        )))
                .isInstanceOf(InvalidProjectDataException.class)
                .hasMessageContaining("Title cannot be empty");
    }

    @Test
    void should_reject_null_description_on_update() {
        ProjectCreated projectCreatedEvent = new ProjectCreated(projectId, workspaceId, userId, title, description, timeline, projectBudgetLimit);
        eventStore.append(projectId.value(), List.of(projectCreatedEvent), -1);

        assertThatThrownBy(() -> handler.handle(new UpdateProjectDetailsCommand(
                projectId,
                "Valid title",
                null  // null description
        )))
                .isInstanceOf(InvalidProjectDataException.class)
                .hasMessageContaining("Description cannot be empty");
    }
}
