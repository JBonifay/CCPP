package fr.joffreybonifay.ccpp.projectplanning.application.command.handler;

import fr.joffreybonifay.ccpp.projectplanning.application.command.command.AddBudgetItemCommand;
import fr.joffreybonifay.ccpp.projectplanning.domain.event.BudgetItemAdded;
import fr.joffreybonifay.ccpp.projectplanning.domain.event.ProjectBudgetCapExceeded;
import fr.joffreybonifay.ccpp.projectplanning.domain.event.ProjectMarkedAsReady;
import fr.joffreybonifay.ccpp.projectplanning.domain.exception.CannotModifyReadyProjectException;
import fr.joffreybonifay.ccpp.projectplanning.domain.exception.InvalidProjectDataException;
import fr.joffreybonifay.ccpp.projectplanning.domain.valueobject.BudgetItemId;
import fr.joffreybonifay.ccpp.shared.domain.event.ProjectCreationRequested;
import fr.joffreybonifay.ccpp.shared.eventstore.AggregateType;
import fr.joffreybonifay.ccpp.shared.eventstore.EventMetadata;
import fr.joffreybonifay.ccpp.shared.eventstore.impl.InMemoryEventStore;
import fr.joffreybonifay.ccpp.shared.domain.exception.CurrencyException;
import fr.joffreybonifay.ccpp.shared.domain.identities.ProjectId;
import fr.joffreybonifay.ccpp.shared.domain.identities.UserId;
import fr.joffreybonifay.ccpp.shared.domain.identities.WorkspaceId;
import fr.joffreybonifay.ccpp.shared.domain.valueobjects.DateRange;
import fr.joffreybonifay.ccpp.shared.domain.valueobjects.Money;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Currency;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AddBudgetItemHandlerTest {

    InMemoryEventStore eventStore = new InMemoryEventStore();
    AddBudgetItemHandler handler = new AddBudgetItemHandler(eventStore);

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
    void should_add_budget_item_to_project() {
        UUID budgetItemId = UUID.randomUUID();
        eventStore.saveEvents(
                projectId.value(),
                AggregateType.PROJECT_PLANNING,
                List.of(
                        new EventMetadata(new ProjectCreationRequested(projectId, workspaceId, userId, title, description, timeline, projectBudgetLimit), null, null, null)),
                -1);

        handler.handle(new AddBudgetItemCommand(
                commandId,
                projectId,
                new BudgetItemId(budgetItemId),
                "Hotel 2 nights",
                new Money(BigDecimal.valueOf(300), Currency.getInstance("USD")),
                correlationId
        ));

        assertThat(eventStore.loadEvents(projectId.value()))
                .last()
                .isEqualTo(new BudgetItemAdded(
                        projectId,
                        new BudgetItemId(budgetItemId),
                        "Hotel 2 nights",
                        new Money(BigDecimal.valueOf(300), Currency.getInstance("USD"))));
    }

    @Test
    void should_mark_project_budget_as_over_limit_when_total_budget_exceeds_cap() {
        eventStore.saveEvents(
                projectId.value(),
                AggregateType.PROJECT_PLANNING,
                List.of(new EventMetadata(new ProjectCreationRequested(projectId, workspaceId, userId, title, description, timeline, new BigDecimal(100)), null, null, null),
                        new EventMetadata(new BudgetItemAdded(projectId, new BudgetItemId(UUID.randomUUID()), "Item 1", new Money(BigDecimal.valueOf(50), Currency.getInstance("USD"))), null, null, null)),
                -1);

        handler.handle(new AddBudgetItemCommand(
                commandId,
                projectId,
                new BudgetItemId(UUID.randomUUID()),
                "Item 2",
                new Money(BigDecimal.valueOf(51), Currency.getInstance("USD")),
                correlationId
        ));

        assertThat(eventStore.loadEvents(projectId.value()))
                .last()
                .isEqualTo(new ProjectBudgetCapExceeded(projectId, new Money(BigDecimal.valueOf(101), Currency.getInstance("USD"))));
    }

    @Test
    void should_prevent_adding_budget_item_when_ready() {
        eventStore.saveEvents(
                projectId.value(),
                AggregateType.PROJECT_PLANNING,
                List.of(new EventMetadata(new ProjectCreationRequested(projectId, workspaceId, userId, title, description, timeline, projectBudgetLimit), null, null, null),
                        new EventMetadata(new ProjectMarkedAsReady(projectId, workspaceId, userId), null, null, null)),
                -1);

        assertThatThrownBy(() -> handler.handle(
                new AddBudgetItemCommand(
                        commandId,
                        projectId,
                        new BudgetItemId(UUID.randomUUID()),
                        "Hotel",
                        new Money(BigDecimal.valueOf(300), Currency.getInstance("USD")),
                        correlationId
                )))
                .isInstanceOf(CannotModifyReadyProjectException.class)
                .hasMessageContaining("Cannot modify project in READY status");
    }

    @Test
    void should_reject_empty_budget_item_description() {
        eventStore.saveEvents(projectId.value(),
                AggregateType.PROJECT_PLANNING,
                List.of(new EventMetadata(new ProjectCreationRequested(projectId, workspaceId, userId, title, description, timeline, projectBudgetLimit), null, null, null)), -1);

        assertThatThrownBy(() -> handler.handle(
                new AddBudgetItemCommand(
                        commandId,
                        projectId,
                        new BudgetItemId(UUID.randomUUID()),
                        "",
                        new Money(BigDecimal.valueOf(300), Currency.getInstance("USD")),
                        correlationId
                )))
                .isInstanceOf(InvalidProjectDataException.class)
                .hasMessageContaining("Budget item description cannot be empty");
    }

    @Test
    void should_fail_to_add_budgetItem_in_different_currency() {
        eventStore.saveEvents(
                projectId.value(),
                AggregateType.PROJECT_PLANNING,
                List.of(
                        new EventMetadata(new ProjectCreationRequested(projectId, workspaceId, userId, title, description, timeline, projectBudgetLimit), null, null, null),
                        new EventMetadata(new BudgetItemAdded(projectId, new BudgetItemId(UUID.randomUUID()), "Item in USD", new Money(BigDecimal.valueOf(100), Currency.getInstance("USD"))), null, null, null)),
                -1);

        assertThatThrownBy(() -> handler.handle(
                new AddBudgetItemCommand(
                        commandId,
                        projectId,
                        new BudgetItemId(UUID.randomUUID()),
                        "Item in EUR",
                        new Money(BigDecimal.valueOf(100), Currency.getInstance("EUR")),
                        correlationId
                )))
                .isInstanceOf(CurrencyException.class)
                .hasMessageContaining("Cannot add budget items with different currencies");
    }
}
