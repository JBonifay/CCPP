package io.joffrey.ccpp.projectplanning.application.command.handler;

import com.ccpp.shared.eventbus.EventBus;
import com.ccpp.shared.eventbus.SimpleEventBus;
import com.ccpp.shared.eventstore.InMemoryEventStore;
import com.ccpp.shared.identities.ProjectId;
import com.ccpp.shared.identities.UserId;
import com.ccpp.shared.identities.WorkspaceId;
import com.ccpp.shared.valueobjects.DateRange;
import com.ccpp.shared.valueobjects.Money;
import io.joffrey.ccpp.projectplanning.application.command.command.UpdateBudgetItemCommand;
import io.joffrey.ccpp.projectplanning.domain.event.BudgetItemAdded;
import io.joffrey.ccpp.projectplanning.domain.event.BudgetItemUpdated;
import io.joffrey.ccpp.projectplanning.domain.event.ProjectCreated;
import io.joffrey.ccpp.projectplanning.domain.event.ProjectMarkedAsReady;
import io.joffrey.ccpp.projectplanning.domain.exception.CannotModifyReadyProjectException;
import io.joffrey.ccpp.projectplanning.domain.valueobject.BudgetItemId;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Currency;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UpdateBudgetItemHandlerTest {

    EventBus eventBus = new SimpleEventBus();
    InMemoryEventStore eventStore = new InMemoryEventStore(eventBus);
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

        eventStore.saveEvents(projectId.value(), List.of(
                new ProjectCreated(projectId, workspaceId, userId, title, description, timeline, projectBudgetLimit),
                new BudgetItemAdded(projectId, budgetItemId, "Hotel 2 nights", new Money(BigDecimal.valueOf(300), Currency.getInstance("USD")))), -1, null, null);

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
                .isEqualTo(new BudgetItemUpdated(projectId, budgetItemId, "Hotel 3 nights", new Money(BigDecimal.valueOf(450), Currency.getInstance("USD"))));
    }

    @Test
    void should_prevent_updating_budget_item_when_ready() {
        var budgetItemId = new BudgetItemId(UUID.randomUUID());

        eventStore.saveEvents(projectId.value(),
                List.of(new ProjectCreated(projectId, workspaceId, userId, title, description, timeline, projectBudgetLimit),
                        new BudgetItemAdded(projectId, budgetItemId, "Hotel", new Money(BigDecimal.valueOf(300), Currency.getInstance("USD"))),
                        new ProjectMarkedAsReady(projectId, workspaceId, userId)), -1, null, null);

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
