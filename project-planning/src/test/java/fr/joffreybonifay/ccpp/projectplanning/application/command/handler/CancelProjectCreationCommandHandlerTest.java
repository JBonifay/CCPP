package fr.joffreybonifay.ccpp.projectplanning.application.command.handler;

import fr.joffreybonifay.ccpp.shared.eventbus.EventBus;
import fr.joffreybonifay.ccpp.shared.eventbus.SimpleEventBus;
import fr.joffreybonifay.ccpp.shared.eventstore.InMemoryEventStore;
import fr.joffreybonifay.ccpp.shared.identities.ProjectId;
import fr.joffreybonifay.ccpp.shared.identities.UserId;
import fr.joffreybonifay.ccpp.shared.identities.WorkspaceId;
import fr.joffreybonifay.ccpp.shared.valueobjects.DateRange;
import fr.joffreybonifay.ccpp.projectplanning.application.command.command.CancelProjectCreationCommand;
import fr.joffreybonifay.ccpp.projectplanning.domain.event.ProjectCreated;
import fr.joffreybonifay.ccpp.projectplanning.domain.event.ProjectCreationCancelled;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class CancelProjectCreationCommandHandlerTest {

    EventBus eventBus = new SimpleEventBus();
    InMemoryEventStore eventStore = new InMemoryEventStore(eventBus);
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
        eventStore.saveEvents(projectId.value(), List.of(new ProjectCreated(projectId, workspaceId, userId, title, description, timeline, projectBudgetLimit)), -1, null, null);

        handler.handle(new CancelProjectCreationCommand(
                commandId,
                projectId,
                "Workspace project limit reached",
                correlationId
        ));

        assertThat(eventStore.loadEvents(projectId.value()))
                .containsExactly(
                        new ProjectCreated(
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
