package fr.joffreybonifay.ccpp.projectplanning.infrastructure.projection;

import fr.joffreybonifay.ccpp.projectplanning.application.query.model.ProjectListDTO;
import fr.joffreybonifay.ccpp.projectplanning.application.query.repository.ProjectListReadRepository;
import fr.joffreybonifay.ccpp.projectplanning.domain.event.*;
import fr.joffreybonifay.ccpp.projectplanning.domain.model.ProjectStatus;
import fr.joffreybonifay.ccpp.projectplanning.domain.valueobject.BudgetItemId;
import fr.joffreybonifay.ccpp.projectplanning.domain.valueobject.ParticipantId;
import fr.joffreybonifay.ccpp.projectplanning.infrastructure.query.InMemoryProjectListReadRepository;
import fr.joffreybonifay.ccpp.shared.domain.event.ProjectCreationRequested;
import fr.joffreybonifay.ccpp.shared.domain.identities.ProjectId;
import fr.joffreybonifay.ccpp.shared.domain.identities.UserId;
import fr.joffreybonifay.ccpp.shared.domain.identities.WorkspaceId;
import fr.joffreybonifay.ccpp.shared.domain.valueobjects.DateRange;
import fr.joffreybonifay.ccpp.shared.domain.valueobjects.Money;
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
        ProjectCreationRequested event = new ProjectCreationRequested(
                projectId,
                workspaceId,
                userId,
                "New Project",
                "Description",
                new DateRange(LocalDate.of(2025, 1, 1), LocalDate.of(2025, 3, 31)),
                BigDecimal.valueOf(1000)
        );

        updater.on(event);

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

        updater.on(event);

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
        updater.on(event);

        // THEN
        var projection = repository.findById(projectId);
        assertThat(projection.get().totalBudget())
                .isEqualByComparingTo(BigDecimal.valueOf(300));
    }

    @Test
    void should_decrement_total_budget_on_budget_item_removed() {
        givenProjectCreated();
        var budgetItemId = new BudgetItemId(UUID.randomUUID());
        updater.on(new BudgetItemAdded(projectId, budgetItemId, "Hotel", Money.of(500, "USD")));

        var event = new BudgetItemRemoved(projectId, null);

        // WHEN
        updater.on(event);

        // THEN
        var projection = repository.findById(projectId);
        assertThat(projection.get().totalBudget())
                .isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    void should_update_budget_on_budget_item_updated() {
        givenProjectCreated();
        var budgetItemId = new BudgetItemId(UUID.randomUUID());
        updater.on(new BudgetItemAdded(projectId, budgetItemId, "Hotel", Money.of(300, "USD")));

        var event = new BudgetItemUpdated(
                projectId,
                budgetItemId,
                "Hotel (updated)",
                Money.of(450, "USD"),
                Money.of(500, "USD")
        );

        updater.on(event);

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
        updater.on(event);

        // THEN
        var projection = repository.findById(projectId);
        assertThat(projection.get().participantCount()).isEqualTo(1);
    }

    @Test
    void should_update_status_on_marked_as_ready() {
        givenProjectCreated();

        var event = new ProjectMarkedAsReady(projectId, workspaceId, userId);

        // WHEN
        updater.on(event);

        // THEN
        var projection = repository.findById(projectId);
        assertThat(projection.get().status()).isEqualTo(ProjectStatus.READY);
    }

    private void givenProjectCreated() {
        updater.on(new ProjectCreationRequested(
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
