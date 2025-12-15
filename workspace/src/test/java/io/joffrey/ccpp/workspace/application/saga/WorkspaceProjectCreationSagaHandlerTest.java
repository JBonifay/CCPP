package io.joffrey.ccpp.workspace.application.saga;

import com.ccpp.shared.command.SpyCommandBus;
import com.ccpp.shared.domain.DomainEvent;
import com.ccpp.shared.domain.event.ProjectCreated;
import com.ccpp.shared.identities.ProjectId;
import com.ccpp.shared.identities.UserId;
import com.ccpp.shared.identities.WorkspaceId;
import com.ccpp.shared.repository.InMemoryEventStore;
import com.ccpp.shared.valueobjects.DateRange;
import io.joffrey.ccpp.workspace.application.command.command.ApproveProjectCreationCommand;
import io.joffrey.ccpp.workspace.application.command.command.RejectProjectCreationCommand;
import io.joffrey.ccpp.workspace.domain.event.WorkspaceCreated;
import io.joffrey.ccpp.workspace.domain.event.WorkspaceProjectCreationApproved;
import io.joffrey.ccpp.workspace.domain.model.SubscriptionTier;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class WorkspaceProjectCreationSagaHandlerTest {

    SpyCommandBus commandBus = new SpyCommandBus();
    InMemoryEventStore workspaceEventStore = new InMemoryEventStore();
    WorkspaceProjectCreationSagaHandler sagaHandler = new WorkspaceProjectCreationSagaHandler(workspaceEventStore, commandBus);

    @Test
    void should_approve_project_creation_when_freemium_under_limit() {
        WorkspaceId workspaceId = new WorkspaceId(UUID.randomUUID());
        setupWorkspaceWithProjectCount(workspaceId, SubscriptionTier.FREEMIUM, 1);

        ProjectId projectId = new ProjectId(UUID.randomUUID());
        UserId userId = new UserId(UUID.randomUUID());
        DateRange timeline = new DateRange(LocalDate.of(2025, 1, 1), LocalDate.of(2025, 3, 31));
        ProjectCreated projectCreated = new ProjectCreated(
                projectId,
                workspaceId,
                userId,
                "Q1 Content",
                "Educational videos",
                timeline,
                BigDecimal.valueOf(1000)
        );

        sagaHandler.onProjectCreated(projectCreated);

        assertThat(commandBus.getExecutedCommands()).containsExactly(
                new ApproveProjectCreationCommand(workspaceId)
        );
    }

    @Test
    void should_reject_project_creation_when_freemium_at_limit() {
        WorkspaceId workspaceId = new WorkspaceId(UUID.randomUUID());
        setupWorkspaceWithProjectCount(workspaceId, SubscriptionTier.FREEMIUM, 2);

        ProjectId projectId = new ProjectId(UUID.randomUUID());
        UserId userId = new UserId(UUID.randomUUID());
        DateRange timeline = new DateRange(LocalDate.of(2025, 1, 1), LocalDate.of(2025, 3, 31));
        ProjectCreated projectCreated = new ProjectCreated(
                projectId,
                workspaceId,
                userId,
                "Q2 Content",
                "More videos",
                timeline,
                BigDecimal.valueOf(1000)
        );

        sagaHandler.onProjectCreated(projectCreated);

        assertThat(commandBus.getExecutedCommands()).containsExactly(
                new RejectProjectCreationCommand(workspaceId, projectId, "Workspace has already reached max projects limit for subscription tier.")
        );
    }

    @Test
    void should_approve_project_creation_when_premium_regardless_of_count() {
        WorkspaceId workspaceId = new WorkspaceId(UUID.randomUUID());
        setupWorkspaceWithProjectCount(workspaceId, SubscriptionTier.PREMIUM, 100);

        ProjectId projectId = new ProjectId(UUID.randomUUID());
        UserId userId = new UserId(UUID.randomUUID());
        DateRange timeline = new DateRange(LocalDate.of(2025, 1, 1), LocalDate.of(2025, 3, 31));
        ProjectCreated projectCreated = new ProjectCreated(
                projectId,
                workspaceId,
                userId,
                "Q3 Content",
                "Unlimited projects",
                timeline,
                BigDecimal.valueOf(5000)
        );

        sagaHandler.onProjectCreated(projectCreated);

        assertThat(commandBus.getExecutedCommands()).containsExactly(
                new ApproveProjectCreationCommand(workspaceId)
        );
    }

    private void setupWorkspaceWithProjectCount(
            WorkspaceId workspaceId,
            SubscriptionTier tier,
            int projectCount
    ) {
        WorkspaceCreated created = new WorkspaceCreated(workspaceId, "Test Workspace", tier);
        List<DomainEvent> events = new ArrayList<>();
        events.add(created);

        for (int i = 0; i < projectCount; i++) {
            events.add(new WorkspaceProjectCreationApproved(workspaceId));
        }

        workspaceEventStore.append(workspaceId.value(), events, -1);
    }
}
