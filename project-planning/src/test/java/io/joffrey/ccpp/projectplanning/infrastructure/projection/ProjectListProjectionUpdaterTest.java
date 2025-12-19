package io.joffrey.ccpp.projectplanning.infrastructure.projection;

import io.joffrey.ccpp.projectplanning.domain.event.ProjectCreated;
import com.ccpp.shared.identities.ProjectId;
import com.ccpp.shared.identities.UserId;
import com.ccpp.shared.identities.WorkspaceId;
import com.ccpp.shared.valueobjects.DateRange;
import com.ccpp.shared.valueobjects.Money;
import io.joffrey.ccpp.projectplanning.application.query.model.ProjectListDTO;
import io.joffrey.ccpp.projectplanning.application.query.repository.ProjectListReadRepository;
import io.joffrey.ccpp.projectplanning.domain.event.*;
import io.joffrey.ccpp.projectplanning.domain.model.ProjectStatus;
import io.joffrey.ccpp.projectplanning.domain.valueobject.BudgetItemId;
import io.joffrey.ccpp.projectplanning.domain.valueobject.ParticipantId;
import io.joffrey.ccpp.projectplanning.infrastructure.query.InMemoryProjectListReadRepository;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ProjectListProjectionUpdaterTest {

    ProjectListReadRepository repository = new InMemoryProjectListReadRepository();
    ProjectListProjectionUpdater updater = new ProjectListProjectionUpdater(repository);
    ProjectId projectId = new ProjectId(UUID.randomUUID());
    WorkspaceId workspaceId = new WorkspaceId(UUID.randomUUID());
    UserId userId = new UserId(UUID.randomUUID());


    @Test
    void should_create_projection_on_project_created() {
        ProjectCreated event = new ProjectCreated(
                projectId,
                workspaceId,
                userId,
                "New Project",
                "Description",
                new DateRange(LocalDate.of(2025, 1, 1), LocalDate.of(2025, 3, 31)),
                BigDecimal.valueOf(1000)
        );

        updater.handle(event);

        Optional<ProjectListDTO> projection = repository.findById(projectId);
        assertThat(projection.get()).isEqualTo(
                new ProjectListDTO(
                        projectId,
                        workspaceId,
                        "New Project",
                        ProjectStatus.PLANNING,
                        BigDecimal.valueOf(1000),
                        0
                ));
    }

    @Test
    void should_update_title_on_details_updated() {
        givenProjectCreated();

        ProjectDetailsUpdated event = new ProjectDetailsUpdated(projectId, "Updated Title", "Updated Description");

        updater.handle(event);

        assertThat(repository.findById(projectId).get().title()).isEqualTo("Updated Title");
    }

    @Test
    void should_increment_total_budget_on_budget_item_added() {
        givenProjectCreated();

        var event = new BudgetItemAdded(
                projectId,
                new BudgetItemId(UUID.randomUUID()),
                "Equipment",
                Money.of(300, "USD")
        );

        // WHEN
        updater.handle(event);

        // THEN
        var projection = repository.findById(projectId);
        assertThat(projection.get().totalBudget())
                .isEqualByComparingTo(BigDecimal.valueOf(300));
    }

    @Test
    void should_decrement_total_budget_on_budget_item_removed() {
        givenProjectCreated();
        var budgetItemId = new BudgetItemId(UUID.randomUUID());
        updater.handle(new BudgetItemAdded(projectId, budgetItemId, "Hotel", Money.of(500, "USD")));

        var event = new BudgetItemRemoved(projectId, budgetItemId);

        // WHEN
        updater.handle(event);

        // THEN
        var projection = repository.findById(projectId);
        assertThat(projection.get().totalBudget())
                .isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    void should_update_budget_on_budget_item_updated() {
        givenProjectCreated();
        var budgetItemId = new BudgetItemId(UUID.randomUUID());
        updater.handle(new BudgetItemAdded(projectId, budgetItemId, "Hotel", Money.of(300, "USD")));

        var event = new BudgetItemUpdated(
                projectId,
                budgetItemId,
                "Hotel (updated)",
                Money.of(450, "USD")  // Only newAmount - no oldAmount!
        );

        updater.handle(event);

        var projection = repository.findById(projectId);
        assertThat(projection.get().totalBudget())
                .isEqualByComparingTo(BigDecimal.valueOf(450));
    }


    @Test
    void should_increment_participant_count_on_invite() {
        givenProjectCreated();

        var event = new ParticipantInvited(
                projectId,
                new ParticipantId(UUID.randomUUID()),
                "John Doe",
                "john@example.com"
        );

        // WHEN
        updater.handle(event);

        // THEN
        var projection = repository.findById(projectId);
        assertThat(projection.get().participantCount()).isEqualTo(1);
    }

    @Test
    void should_update_status_on_marked_as_ready() {
        givenProjectCreated();

        var event = new ProjectMarkedAsReady(projectId, workspaceId, userId);

        // WHEN
        updater.handle(event);

        // THEN
        var projection = repository.findById(projectId);
        assertThat(projection.get().status()).isEqualTo(ProjectStatus.READY);
    }

    private void givenProjectCreated() {
        updater.handle(new ProjectCreated(
                projectId,
                workspaceId,
                userId,
                "Test Project",
                "Description",
                new DateRange(LocalDate.of(2025, 1, 1), LocalDate.of(2025, 3, 31)),
                BigDecimal.valueOf(1000)
        ));
    }
}
