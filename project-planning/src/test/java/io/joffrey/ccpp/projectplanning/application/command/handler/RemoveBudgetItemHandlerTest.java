package io.joffrey.ccpp.projectplanning.application.command.handler;

import com.ccpp.shared.identities.ProjectId;
import com.ccpp.shared.identities.UserId;
import com.ccpp.shared.identities.WorkspaceId;
import com.ccpp.shared.valueobjects.DateRange;
import com.ccpp.shared.valueobjects.Money;
import io.joffrey.ccpp.projectplanning.application.command.RemoveBudgetItemCommand;
import io.joffrey.ccpp.projectplanning.domain.event.BudgetItemAdded;
import io.joffrey.ccpp.projectplanning.domain.event.BudgetItemRemoved;
import io.joffrey.ccpp.projectplanning.domain.event.ProjectCreated;
import io.joffrey.ccpp.projectplanning.domain.event.ProjectMarkedAsReady;
import io.joffrey.ccpp.projectplanning.domain.exception.CannotModifyReadyProjectException;
import io.joffrey.ccpp.projectplanning.domain.valueobject.BudgetItemId;
import io.joffrey.ccpp.projectplanning.infrastructure.event.InMemoryEventStore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Currency;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RemoveBudgetItemHandlerTest {

    private InMemoryEventStore eventStore;
    private RemoveBudgetItemHandler handler;

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
        handler = new RemoveBudgetItemHandler(eventStore);

        workspaceId = new WorkspaceId(UUID.randomUUID());
        userId = new UserId(UUID.randomUUID());
        projectId = new ProjectId(UUID.randomUUID());
        timeline = new DateRange(LocalDate.of(2025, 1, 1), LocalDate.of(2025, 3, 31));
        title = "Q1 Video Series";
        description = "Educational content";
        projectBudgetLimit = BigDecimal.valueOf(1000);
    }

    @Test
    void should_remove_budget_item() {
        // GIVEN - project with budget item
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

        eventStore.append(
                projectId.value(),
                java.util.List.of(projectCreatedEvent, budgetItemAddedEvent),
                -1
        );

        var command = new RemoveBudgetItemCommand(projectId, budgetItemId);

        // WHEN
        handler.handle(command);

        // THEN
        var events = eventStore.readStream(projectId.value());
        assertThat(events).hasSize(3);
        assertThat(events.get(2)).isInstanceOf(BudgetItemRemoved.class);

        var budgetRemovedEvent = (BudgetItemRemoved) events.get(2);
        assertThat(budgetRemovedEvent.projectId()).isEqualTo(projectId);
        assertThat(budgetRemovedEvent.budgetItemId()).isEqualTo(budgetItemId);
    }

    @Test
    void should_prevent_removing_budget_item_when_ready() {
        // GIVEN - project is marked as READY with budget item
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

        var command = new RemoveBudgetItemCommand(projectId, budgetItemId);

        // WHEN & THEN
        assertThatThrownBy(() -> handler.handle(command))
                .isInstanceOf(CannotModifyReadyProjectException.class)
                .hasMessageContaining("Cannot modify project in READY status");

        var events = eventStore.readStream(projectId.value());
        assertThat(events).hasSize(3);
    }
}
