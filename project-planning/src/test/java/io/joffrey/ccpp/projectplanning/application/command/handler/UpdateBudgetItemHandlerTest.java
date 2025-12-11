package io.joffrey.ccpp.projectplanning.application.command.handler;

import com.ccpp.shared.identities.ProjectId;
import com.ccpp.shared.identities.UserId;
import com.ccpp.shared.identities.WorkspaceId;
import com.ccpp.shared.valueobjects.DateRange;
import com.ccpp.shared.valueobjects.Money;
import io.joffrey.ccpp.projectplanning.application.command.UpdateBudgetItemCommand;
import io.joffrey.ccpp.projectplanning.domain.event.BudgetItemAdded;
import io.joffrey.ccpp.projectplanning.domain.event.BudgetItemUpdated;
import io.joffrey.ccpp.projectplanning.domain.event.ProjectCreated;
import io.joffrey.ccpp.projectplanning.domain.event.ProjectMarkedAsReady;
import io.joffrey.ccpp.projectplanning.domain.exception.CannotModifyReadyProjectException;
import io.joffrey.ccpp.projectplanning.domain.valueobject.BudgetItemId;
import io.joffrey.ccpp.projectplanning.infrastructure.event.InMemoryEventStore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Currency;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UpdateBudgetItemHandlerTest {

    private InMemoryEventStore eventStore;
    private UpdateBudgetItemHandler handler;

    private WorkspaceId workspaceId;
    private UserId userId;
    private ProjectId projectId;
    private DateRange timeline;
    private String title;
    private String description;
    private BigDecimal projectBudgetLimit;

    @BeforeEach
    void setUp() {
        eventStore = new InMemoryEventStore();
        handler = new UpdateBudgetItemHandler(eventStore);

        workspaceId = new WorkspaceId(UUID.randomUUID());
        userId = new UserId(UUID.randomUUID());
        projectId = new ProjectId(UUID.randomUUID());
        timeline = new DateRange(LocalDate.of(2025, 1, 1), LocalDate.of(2025, 3, 31));
        title = "Q1 Video Series";
        description = "Educational content";
        projectBudgetLimit = BigDecimal.valueOf(1000);
    }

    @Test
    void should_update_budget_item() {
        // GIVEN - project with budget item
        var budgetItemId = new BudgetItemId(UUID.randomUUID());
        var originalAmount = new Money(BigDecimal.valueOf(300), Currency.getInstance("USD"));

        var projectCreatedEvent = new ProjectCreated(
                workspaceId,
                userId,
                projectId,
                title,
                description,
                timeline,
                projectBudgetLimit
        );
        var budgetItemAddedEvent = new BudgetItemAdded(projectId, budgetItemId, "Hotel 2 nights", originalAmount);

        eventStore.append(
                projectId.value(),
                java.util.List.of(projectCreatedEvent, budgetItemAddedEvent),
                -1
        );

        var newAmount = new Money(BigDecimal.valueOf(450), Currency.getInstance("USD"));
        var command = new UpdateBudgetItemCommand(projectId, budgetItemId, "Hotel 3 nights", newAmount);

        // WHEN
        handler.handle(command);

        // THEN
        assertThat(eventStore.readStream(projectId.value()))
                .hasSize(3)
                .satisfies(events -> {
                    assertThat(events.get(2)).isInstanceOf(BudgetItemUpdated.class);
                    var budgetUpdatedEvent = (BudgetItemUpdated) events.get(2);
                    assertThat(budgetUpdatedEvent.getProjectId()).isEqualTo(projectId);
                    assertThat(budgetUpdatedEvent.getBudgetItemId()).isEqualTo(budgetItemId);
                    assertThat(budgetUpdatedEvent.getDescription()).isEqualTo("Hotel 3 nights");
                    assertThat(budgetUpdatedEvent.getNewAmount()).isEqualTo(newAmount);
                });
    }

    @Test
    void should_prevent_updating_budget_item_when_ready() {
        // GIVEN - project is marked as READY
        var budgetItemId = new BudgetItemId(UUID.randomUUID());
        var amount = new Money(BigDecimal.valueOf(300), Currency.getInstance("USD"));

        var projectCreatedEvent = new ProjectCreated(
                workspaceId,
                userId,
                projectId,
                title,
                description,
                timeline,
                projectBudgetLimit
        );
        var budgetItemAddedEvent = new BudgetItemAdded(projectId, budgetItemId, "Hotel", amount);
        var projectMarkedAsReadyEvent = new ProjectMarkedAsReady(projectId, workspaceId, userId);

        eventStore.append(
                projectId.value(),
                java.util.List.of(projectCreatedEvent, budgetItemAddedEvent, projectMarkedAsReadyEvent),
                -1
        );

        var newAmount = new Money(BigDecimal.valueOf(450), Currency.getInstance("USD"));
        var command = new UpdateBudgetItemCommand(projectId, budgetItemId, "Hotel updated", newAmount);

        // WHEN & THEN
        assertThatThrownBy(() -> handler.handle(command))
                .isInstanceOf(CannotModifyReadyProjectException.class)
                .hasMessageContaining("Cannot modify project in READY status");

        assertThat(eventStore.readStream(projectId.value())).hasSize(3);
    }
}
