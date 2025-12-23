package fr.joffreybonifay.ccpp.projectplanning.application.command.handler;

import fr.joffreybonifay.ccpp.projectplanning.application.command.command.UpdateBudgetItemCommand;
import fr.joffreybonifay.ccpp.projectplanning.domain.event.BudgetItemAdded;
import fr.joffreybonifay.ccpp.projectplanning.domain.event.BudgetItemUpdated;
import fr.joffreybonifay.ccpp.projectplanning.domain.event.ProjectMarkedAsReady;
import fr.joffreybonifay.ccpp.projectplanning.domain.exception.CannotModifyReadyProjectException;
import fr.joffreybonifay.ccpp.projectplanning.domain.valueobject.BudgetItemId;
import fr.joffreybonifay.ccpp.shared.domain.event.ProjectCreationRequested;
import fr.joffreybonifay.ccpp.shared.domain.identities.ProjectId;
import fr.joffreybonifay.ccpp.shared.domain.identities.UserId;
import fr.joffreybonifay.ccpp.shared.domain.identities.WorkspaceId;
import fr.joffreybonifay.ccpp.shared.domain.valueobjects.DateRange;
import fr.joffreybonifay.ccpp.shared.domain.valueobjects.Money;
import fr.joffreybonifay.ccpp.shared.eventstore.AggregateType;
import fr.joffreybonifay.ccpp.shared.eventstore.EventMetadata;
import fr.joffreybonifay.ccpp.shared.eventstore.impl.InMemoryEventStore;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Currency;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UpdateBudgetItemHandlerTest {

    InMemoryEventStore eventStore = new InMemoryEventStore();
    UpdateBudgetItemHandler handler = new UpdateBudgetItemHandler(eventStore);

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
    void should_update_budget_item() {
        var budgetItemId = new BudgetItemId(UUID.randomUUID());

        eventStore.saveEvents(projectId.value(),
                AggregateType.PROJECT_PLANNING,
                List.of(
                        new EventMetadata(new ProjectCreationRequested(projectId, workspaceId, userId, title, description, timeline, projectBudgetLimit), null, null, null),
                        new EventMetadata(new BudgetItemAdded(projectId, budgetItemId, "Hotel 2 nights", new Money(BigDecimal.valueOf(300), Currency.getInstance("USD"))), null, null, null)), -1);

        handler.handle(new UpdateBudgetItemCommand(
                commandId,
                projectId,
                budgetItemId,
                "Hotel 3 nights",
                new Money(BigDecimal.valueOf(450), Currency.getInstance("USD")),
                correlationId
        ));

        assertThat(eventStore.loadEvents(projectId.value()))
                .last()
                .isEqualTo(new BudgetItemUpdated(
                        projectId,
                        budgetItemId,
                        "Hotel 3 nights",
                        new Money(BigDecimal.valueOf(300), Currency.getInstance("USD")),
                        new Money(BigDecimal.valueOf(450), Currency.getInstance("USD"))));
    }

    @Test
    void should_prevent_updating_budget_item_when_ready() {
        var budgetItemId = new BudgetItemId(UUID.randomUUID());

        eventStore.saveEvents(projectId.value(),
                AggregateType.PROJECT_PLANNING,
                List.of(new EventMetadata(new ProjectCreationRequested(projectId, workspaceId, userId, title, description, timeline, projectBudgetLimit), null, null, null),
                        new EventMetadata(new BudgetItemAdded(projectId, budgetItemId, "Hotel", new Money(BigDecimal.valueOf(300), Currency.getInstance("USD"))), null, null, null),
                        new EventMetadata(new ProjectMarkedAsReady(projectId, workspaceId, userId), null, null, null)),
                -1);

        assertThatThrownBy(() -> handler.handle(new UpdateBudgetItemCommand(
                commandId,
                projectId,
                budgetItemId,
                "Hotel updated",
                new Money(BigDecimal.valueOf(450), Currency.getInstance("USD")),
                correlationId
        )))
                .isInstanceOf(CannotModifyReadyProjectException.class)
                .hasMessageContaining("Cannot modify project in READY status");
    }
}
