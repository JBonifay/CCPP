package io.joffrey.ccpp.projectplanning.application.saga;

import com.ccpp.shared.command.SpyCommandBus;
import com.ccpp.shared.domain.event.ProjectCreated;
import com.ccpp.shared.domain.event.WorkspaceProjectCreationRejected;
import com.ccpp.shared.identities.ProjectId;
import com.ccpp.shared.identities.UserId;
import com.ccpp.shared.identities.WorkspaceId;
import com.ccpp.shared.repository.InMemoryEventStore;
import com.ccpp.shared.valueobjects.DateRange;
import io.joffrey.ccpp.projectplanning.domain.event.ProjectCreationCancelled;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ProjectCreationRejectionSagaHandlerTest {

    InMemoryEventStore projectEventStore = new InMemoryEventStore();
    SpyCommandBus commandBus = new SpyCommandBus();
    ProjectCreationRejectionSagaHandler sagaHandler = new ProjectCreationRejectionSagaHandler(commandBus);

    @Test
    void should_cancel_project_when_workspace_rejects_creation() {
        WorkspaceId workspaceId = new WorkspaceId(UUID.randomUUID());
        ProjectId projectId = new ProjectId(UUID.randomUUID());
        UserId userId = new UserId(UUID.randomUUID());
        DateRange timeline = new DateRange(LocalDate.of(2025, 1, 1), LocalDate.of(2025, 3, 31));

        projectEventStore.append(projectId.value(), List.of(new ProjectCreated(projectId, workspaceId, userId, "Q1 Content", "Educational videos", timeline, BigDecimal.valueOf(1000))), -1);


        sagaHandler.onProjectCreationRejected(new WorkspaceProjectCreationRejected(workspaceId, projectId, "FREEMIUM limit reached"));

        assertThat(projectEventStore.readStream(projectId.value()))
                .last()
                .isEqualTo(
                        new ProjectCreationCancelled(projectId, "FREEMIUM limit reached")
                );
    }
}
