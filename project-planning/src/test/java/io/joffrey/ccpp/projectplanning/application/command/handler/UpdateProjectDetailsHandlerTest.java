package io.joffrey.ccpp.projectplanning.application.command.handler;

import com.ccpp.shared.identities.ProjectId;
import com.ccpp.shared.identities.UserId;
import com.ccpp.shared.identities.WorkspaceId;
import com.ccpp.shared.valueobjects.DateRange;
import io.joffrey.ccpp.projectplanning.application.command.command.UpdateProjectDetailsCommand;
import io.joffrey.ccpp.projectplanning.domain.event.ProjectCreated;
import io.joffrey.ccpp.projectplanning.domain.event.ProjectDetailsUpdated;
import io.joffrey.ccpp.projectplanning.domain.exception.InvalidProjectDataException;
import com.ccpp.shared.eventstore.InMemoryEventStore;
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
        eventStore.saveEvents(projectId.value(), List.of(new ProjectCreated(projectId, workspaceId, userId, title, description, timeline, projectBudgetLimit)), -1, null,null);

        handler.handle(new UpdateProjectDetailsCommand(projectId, "Q2 Video Series", "Updated educational content"));

        assertThat(eventStore.loadEvents(projectId.value()))
                .last()
                .isEqualTo(new ProjectDetailsUpdated(projectId, "Q2 Video Series", "Updated educational content"));
    }

    @Test
    void should_reject_empty_title_on_update() {
        eventStore.saveEvents(projectId.value(), List.of(new ProjectCreated(projectId, workspaceId, userId, title, description, timeline, projectBudgetLimit)), -1, null,null);

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
        eventStore.saveEvents(projectId.value(), List.of(new ProjectCreated(projectId, workspaceId, userId, title, description, timeline, projectBudgetLimit)), -1, null,null);

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
        eventStore.saveEvents(projectId.value(), List.of(projectCreatedEvent), -1, null,null);

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
        eventStore.saveEvents(projectId.value(), List.of(projectCreatedEvent), -1, null,null);

        assertThatThrownBy(() -> handler.handle(new UpdateProjectDetailsCommand(
                projectId,
                "Valid title",
                null  // null description
        )))
                .isInstanceOf(InvalidProjectDataException.class)
                .hasMessageContaining("Description cannot be empty");
    }
}
