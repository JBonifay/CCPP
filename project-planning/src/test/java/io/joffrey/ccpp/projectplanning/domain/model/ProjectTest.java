package io.joffrey.ccpp.projectplanning.domain.model;

import com.ccpp.shared.exception.CurrencyException;
import com.ccpp.shared.exception.DateRangeException;
import com.ccpp.shared.identities.ProjectId;
import com.ccpp.shared.identities.UserId;
import com.ccpp.shared.identities.WorkspaceId;
import com.ccpp.shared.valueobjects.DateRange;
import com.ccpp.shared.valueobjects.Money;
import io.joffrey.ccpp.projectplanning.domain.event.*;
import io.joffrey.ccpp.projectplanning.domain.exception.CannotModifyReadyProjectException;
import io.joffrey.ccpp.projectplanning.domain.exception.InvalidProjectDataException;
import io.joffrey.ccpp.projectplanning.domain.valueobject.BudgetItemId;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Currency;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ProjectTest {

    WorkspaceId workspaceId = new WorkspaceId(UUID.randomUUID());
    UserId userId = new UserId(UUID.randomUUID());
    ProjectId projectId = new ProjectId(UUID.randomUUID());
    DateRange timeline = new DateRange(LocalDate.of(2025, 1, 1), LocalDate.of(2025, 3, 31));
    String title = "Q1 Video Series";
    String description = "Educational content";

    @Test
    void should_create_project_with_valid_data() {
        var project = Project.create(
                workspaceId,
                userId,
                projectId,
                title,
                description,
                timeline
        );

        assertThat(project.uncommittedEvents()).containsExactly(
                new ProjectCreated(
                        workspaceId,
                        userId,
                        projectId,
                        title,
                        description,
                        timeline
                )
        );
    }

    @Test
    void should_reject_project_with_invalid_timeline() {
        assertThatThrownBy(() ->
                Project.create(
                        workspaceId,
                        userId,
                        projectId,
                        title,
                        description,
                        new DateRange(LocalDate.of(2025, 3, 31), LocalDate.of(2025, 1, 1))
                )
        )
                .isInstanceOf(DateRangeException.class)
                .hasMessageContaining("Start date must be before end date");
    }

    @Test
    void should_reject_project_with_empty_title() {
        assertThatThrownBy(() ->
                Project.create(
                        workspaceId,
                        userId,
                        projectId,
                        "",
                        description,
                        timeline
                )
        )
                .isInstanceOf(InvalidProjectDataException.class)
                .hasMessageContaining("Title cannot be empty");
    }

    @Test
    void should_reject_project_with_null_title() {
        assertThatThrownBy(() ->
                Project.create(
                        workspaceId,
                        userId,
                        projectId,
                        null,
                        description,
                        timeline
                )
        )
                .isInstanceOf(InvalidProjectDataException.class)
                .hasMessageContaining("Title cannot be empty");
    }

    @Test
    void should_reject_project_with_empty_description() {
        assertThatThrownBy(() ->
                Project.create(
                        workspaceId,
                        userId,
                        projectId,
                        title,
                        "",
                        timeline
                )
        )
                .isInstanceOf(InvalidProjectDataException.class)
                .hasMessageContaining("Description cannot be empty");
    }

    @Test
    void should_reject_project_with_null_description() {
        assertThatThrownBy(() ->
                Project.create(
                        workspaceId,
                        userId,
                        projectId,
                        title,
                        null,
                        timeline
                )
        )
                .isInstanceOf(InvalidProjectDataException.class)
                .hasMessageContaining("Description cannot be empty");
    }

    @Test
    void should_add_budget_item_to_project() {
        Project project = Project.loadFromHistory(List.of(new ProjectCreated(workspaceId, userId, projectId, title, description, timeline)));

        var budgetItemId = new BudgetItemId(UUID.randomUUID());
        var amount = new Money(BigDecimal.valueOf(300), Currency.getInstance("USD"));

        project.addBudgetItem(budgetItemId, "Hotel 2 nights", amount);

        assertThat(project.uncommittedEvents()).containsExactly(
                new BudgetItemAdded(
                        projectId,
                        budgetItemId,
                        "Hotel 2 nights",
                        amount)
        );
    }

    @Test
    void should_add_multiple_budget_items() {
        var project = Project.loadFromHistory(List.of(new ProjectCreated(workspaceId, userId, projectId, title, description, timeline)));

        var item1Id = new BudgetItemId(UUID.randomUUID());
        var item2Id = new BudgetItemId(UUID.randomUUID());
        var amount1 = new Money(BigDecimal.valueOf(300), Currency.getInstance("USD"));
        var amount2 = new Money(BigDecimal.valueOf(150), Currency.getInstance("USD"));

        project.addBudgetItem(item1Id, "Hotel", amount1);
        project.addBudgetItem(item2Id, "Equipment", amount2);

        assertThat(project.uncommittedEvents())
                .hasSize(2)
                .containsExactly(
                        new BudgetItemAdded(projectId, item1Id, "Hotel", amount1),
                        new BudgetItemAdded(projectId, item2Id, "Equipment", amount2)
                );
    }

    @Test
    void should_remove_budget_item() {
        var budgetItemId = new BudgetItemId(UUID.randomUUID());
        var amount = new Money(BigDecimal.valueOf(300), Currency.getInstance("USD"));
        var project = Project.loadFromHistory(List.of(
                new ProjectCreated(workspaceId, userId, projectId, title, description, timeline),
                new BudgetItemAdded(projectId, budgetItemId, "Hotel", amount)
        ));

        project.removeBudgetItem(budgetItemId);

        assertThat(project.uncommittedEvents()).containsExactly(
                new BudgetItemRemoved(projectId, budgetItemId)
        );
    }

    @Test
    void should_prevent_removing_budget_item_when_ready() {
        var budgetItemId = new BudgetItemId(UUID.randomUUID());
        var amount = new Money(BigDecimal.valueOf(300), Currency.getInstance("USD"));
        var project = Project.loadFromHistory(List.of(
                new ProjectCreated(workspaceId, userId, projectId, title, description, timeline),
                new BudgetItemAdded(projectId, budgetItemId, "Hotel", amount),
                new ProjectMarkedAsReady(projectId, workspaceId, userId)
        ));

        assertThatThrownBy(() ->
                project.removeBudgetItem(budgetItemId)
        )
                .isInstanceOf(CannotModifyReadyProjectException.class)
                .hasMessageContaining("Cannot modify project in READY status");
    }

    @Test
    void should_update_budget_item() {
        var budgetItemId = new BudgetItemId(UUID.randomUUID());
        var originalAmount = new Money(BigDecimal.valueOf(300), Currency.getInstance("USD"));

        var project = Project.loadFromHistory(List.of(
                new ProjectCreated(workspaceId, userId, projectId, title, description, timeline),
                new BudgetItemAdded(projectId, budgetItemId, "Hotel 2 nights", originalAmount)
        ));

        var newAmount = new Money(BigDecimal.valueOf(450), Currency.getInstance("USD"));
        project.updateBudgetItem(budgetItemId, "Hotel 3 nights", newAmount);

        assertThat(project.uncommittedEvents()).containsExactly(
                new BudgetItemUpdated(projectId, budgetItemId, "Hotel 3 nights", newAmount)
        );
    }

    @Test
    void should_prevent_updating_budget_item_when_ready() {
        var budgetItemId = new BudgetItemId(UUID.randomUUID());
        var amount = new Money(BigDecimal.valueOf(300), Currency.getInstance("USD"));
        var project = Project.loadFromHistory(List.of(
                new ProjectCreated(workspaceId, userId, projectId, title, description, timeline),
                new BudgetItemAdded(projectId, budgetItemId, "Hotel", amount),
                new ProjectMarkedAsReady(projectId, workspaceId, userId)
        ));

        assertThatThrownBy(() ->
                project.updateBudgetItem(
                        budgetItemId,
                        "Hotel Updated",
                        new Money(BigDecimal.valueOf(450), Currency.getInstance("USD"))
                )
        )
                .isInstanceOf(CannotModifyReadyProjectException.class)
                .hasMessageContaining("Cannot modify project in READY status");
    }


    @Test
    void should_fail_to_add_budgetItem_in_different_currency() {
        var project = Project.loadFromHistory(List.of(
                new ProjectCreated(workspaceId, userId, projectId, title, description, timeline),
                new BudgetItemAdded(projectId, new BudgetItemId(UUID.randomUUID()), "Hotel 2 nights", new Money(BigDecimal.valueOf(300), Currency.getInstance("USD")))
        ));

        assertThatThrownBy(() -> project.addBudgetItem(new BudgetItemId(UUID.randomUUID()), "INVALID", new Money(BigDecimal.valueOf(300), Currency.getInstance("EUR"))))
                .isInstanceOf(CurrencyException.class)
                .hasMessageContaining("Cannot add budget items with different currencies.");
    }

    @Test
    void should_reject_empty_budget_item_description() {
        var project = Project.loadFromHistory(List.of(
                new ProjectCreated(workspaceId, userId, projectId, title, description, timeline)
        ));

        assertThatThrownBy(() ->
                project.addBudgetItem(
                        new BudgetItemId(UUID.randomUUID()),
                        "",
                        new Money(BigDecimal.valueOf(300), Currency.getInstance("USD"))
                )
        )
                .isInstanceOf(InvalidProjectDataException.class)
                .hasMessageContaining("Budget item description cannot be empty");
    }


    @Test
    void should_mark_project_as_ready() {
        var project = Project.loadFromHistory(List.of(new ProjectCreated(workspaceId, userId, projectId, title, description, timeline)));

        project.markAsReady(userId);

        assertThat(project.uncommittedEvents()).containsExactly(
                new ProjectMarkedAsReady(projectId, workspaceId, userId)
        );
    }

    @Test
    void should_prevent_adding_budget_item_when_ready() {
        var project = Project.loadFromHistory(List.of(
                new ProjectCreated(workspaceId, userId, projectId, title, description, timeline),
                new ProjectMarkedAsReady(projectId, workspaceId, userId)
        ));


        assertThatThrownBy(() ->
                project.addBudgetItem(
                        new BudgetItemId(UUID.randomUUID()),
                        "Hotel",
                        new Money(BigDecimal.valueOf(300), Currency.getInstance("USD"))
                )
        )
                .isInstanceOf(CannotModifyReadyProjectException.class)
                .hasMessageContaining("Cannot modify project in READY status");
    }

    @Test
    void should_be_idempotent_when_marking_ready_project_as_ready() {
        var project = Project.loadFromHistory(List.of(
                new ProjectCreated(workspaceId, userId, projectId, title, description, timeline),
                new ProjectMarkedAsReady(projectId, workspaceId, userId)
        ));

        project.markAsReady(userId);

        assertThat(project.uncommittedEvents()).isEmpty();
    }

    @Test
    void should_change_timeline_when_planning() {
        var project = Project.loadFromHistory(List.of(
                new ProjectCreated(workspaceId, userId, projectId, title, description, timeline)
        ));
        var newTimeline = new DateRange(LocalDate.of(2025, 2, 1), LocalDate.of(2025, 4, 30));

        // When
        project.changeTimeline(newTimeline);

        // Then
        assertThat(project.uncommittedEvents()).containsExactly(
                new ProjectTimelineChanged(projectId, newTimeline)
        );
    }

    @Test
    void should_prevent_changing_timeline_when_ready() {
        var project = Project.loadFromHistory(List.of(
                new ProjectCreated(workspaceId, userId, projectId, title, description, timeline),
                new ProjectMarkedAsReady(projectId, workspaceId, userId)
        ));

        var newTimeline = new DateRange(LocalDate.of(2025, 2, 1), LocalDate.of(2025, 4, 30));

        assertThatThrownBy(() ->
                project.changeTimeline(newTimeline)
        )
                .isInstanceOf(CannotModifyReadyProjectException.class)
                .hasMessageContaining("Cannot modify project in READY status");
    }


}
