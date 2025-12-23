package fr.joffreybonifay.ccpp.projectplanning.application.command.handler;

import fr.joffreybonifay.ccpp.projectplanning.application.command.command.UpdateDetailsCommand;
import fr.joffreybonifay.ccpp.projectplanning.domain.event.ProjectDetailsUpdated;
import fr.joffreybonifay.ccpp.projectplanning.domain.exception.InvalidProjectDataException;
import fr.joffreybonifay.ccpp.shared.domain.event.ProjectCreationRequested;
import fr.joffreybonifay.ccpp.shared.eventstore.AggregateType;
import fr.joffreybonifay.ccpp.shared.eventstore.EventMetadata;
import fr.joffreybonifay.ccpp.shared.eventstore.impl.InMemoryEventStore;
import fr.joffreybonifay.ccpp.shared.domain.identities.ProjectId;
import fr.joffreybonifay.ccpp.shared.domain.identities.UserId;
import fr.joffreybonifay.ccpp.shared.domain.identities.WorkspaceId;
import fr.joffreybonifay.ccpp.shared.domain.valueobjects.DateRange;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UpdateDetailsHandlerTest {

    InMemoryEventStore eventStore = new InMemoryEventStore();
    UpdateDetailsHandler handler = new UpdateDetailsHandler(eventStore);

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
    void should_update_project_details() {
        eventStore.saveEvents(
                projectId.value(),
                AggregateType.PROJECT_PLANNING,
                List.of(new EventMetadata(new ProjectCreationRequested(projectId, workspaceId, userId, title, description, timeline, projectBudgetLimit), null, null, null)),
                -1);

        handler.handle(new UpdateDetailsCommand(
                commandId,
                projectId,
                "Q2 Video Series",
                "Updated educational content",
                correlationId
        ));

        assertThat(eventStore.loadEvents(projectId.value()))
                .last()
                .isEqualTo(new ProjectDetailsUpdated(projectId, "Q2 Video Series", "Updated educational content"));
    }

    @Test
    void should_reject_empty_title_on_update() {
        eventStore.saveEvents(projectId.value(),
                AggregateType.PROJECT_PLANNING,
                List.of(
                        new EventMetadata(new ProjectCreationRequested(projectId, workspaceId, userId, title, description, timeline, projectBudgetLimit), null, null, null)),
                -1);

        assertThatThrownBy(() -> handler.handle(
                new UpdateDetailsCommand(
                        commandId,
                        projectId,
                        "",  // empty title
                        "Valid description",
                        correlationId
                )
        )).isInstanceOf(InvalidProjectDataException.class)
                .hasMessageContaining("Title cannot be empty");
    }

    @Test
    void should_reject_empty_description_on_update() {
        eventStore.saveEvents(projectId.value(),
                AggregateType.PROJECT_PLANNING,
                List.of(new EventMetadata(new ProjectCreationRequested(projectId, workspaceId, userId, title, description, timeline, projectBudgetLimit), null, null, null)),
                -1);

        assertThatThrownBy(() -> handler.handle(new UpdateDetailsCommand(
                commandId,
                projectId,
                "Valid title",
                "",  // empty description
                correlationId
        )))
                .isInstanceOf(InvalidProjectDataException.class)
                .hasMessageContaining("Description cannot be empty");
    }

    @Test
    void should_reject_null_title_on_update() {
        eventStore.saveEvents(projectId.value(),
                AggregateType.PROJECT_PLANNING,
                List.of(new EventMetadata(new ProjectCreationRequested(projectId, workspaceId, userId, title, description, timeline, projectBudgetLimit), null, null, null)),
                -1);

        assertThatThrownBy(() -> handler.handle(new UpdateDetailsCommand(
                commandId,
                projectId,
                null,  // null title
                "Valid description",
                correlationId
        )))
                .isInstanceOf(InvalidProjectDataException.class)
                .hasMessageContaining("Title cannot be empty");
    }

    @Test
    void should_reject_null_description_on_update() {
        eventStore.saveEvents(projectId.value(),
                AggregateType.PROJECT_PLANNING,
                List.of(new EventMetadata(new ProjectCreationRequested(projectId, workspaceId, userId, title, description, timeline, projectBudgetLimit), null, null, null)),
                -1);

        assertThatThrownBy(() -> handler.handle(new UpdateDetailsCommand(
                commandId,
                projectId,
                "Valid title",
                null, // null description
                correlationId
        )))
                .isInstanceOf(InvalidProjectDataException.class)
                .hasMessageContaining("Description cannot be empty");
    }
}
