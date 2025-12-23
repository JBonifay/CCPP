package fr.joffreybonifay.ccpp.projectplanning.application.command.handler;

import fr.joffreybonifay.ccpp.projectplanning.application.command.command.CancelProjectCreationCommand;
import fr.joffreybonifay.ccpp.projectplanning.domain.event.ProjectCreationCancelled;
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

class CancelProjectCreationCommandHandlerTest {

    InMemoryEventStore eventStore = new InMemoryEventStore();
    CancelProjectCreationHandler handler = new CancelProjectCreationHandler(eventStore);

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
    void should_cancel_project_creation_with_valid_reason() {
        eventStore.saveEvents(projectId.value(),
                AggregateType.PROJECT_PLANNING,
                List.of(new EventMetadata(new ProjectCreationRequested(projectId, workspaceId, userId, title, description, timeline, projectBudgetLimit), null, null, null)),
                -1);

        handler.handle(new CancelProjectCreationCommand(
                commandId,
                projectId,
                "Workspace project limit reached",
                correlationId
        ));

        assertThat(eventStore.loadEvents(projectId.value()))
                .containsExactly(
                        new ProjectCreationRequested(
                                projectId,
                                workspaceId,
                                userId,
                                title,
                                description,
                                timeline,
                                projectBudgetLimit
                        ),
                        new ProjectCreationCancelled(projectId, "Workspace project limit reached")
                );
    }
}
