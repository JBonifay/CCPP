package io.joffrey.ccpp.projectplanning.application.command.handler;

import com.ccpp.shared.eventstore.InMemoryEventStore;
import com.ccpp.shared.identities.ProjectId;
import com.ccpp.shared.identities.UserId;
import com.ccpp.shared.identities.WorkspaceId;
import com.ccpp.shared.valueobjects.DateRange;
import io.joffrey.ccpp.projectplanning.application.command.command.CancelProjectCreationCommand;
import io.joffrey.ccpp.projectplanning.domain.event.ProjectCreated;
import io.joffrey.ccpp.projectplanning.domain.event.ProjectCreationCancelled;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class CancelProjectCreationCommandHandlerTest {

    InMemoryEventStore eventStore = new InMemoryEventStore();
    CancelProjectCreationCommandHandler handler = new CancelProjectCreationCommandHandler(eventStore);

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
