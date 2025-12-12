//package io.joffrey.ccpp.projectplanning.application.command.handler;
//
//import com.ccpp.shared.exception.CurrencyException;
//import com.ccpp.shared.identities.ProjectId;
//import com.ccpp.shared.identities.UserId;
//import com.ccpp.shared.identities.WorkspaceId;
//import com.ccpp.shared.valueobjects.DateRange;
//import com.ccpp.shared.valueobjects.Money;
//import io.joffrey.ccpp.projectplanning.application.command.AddBudgetItemCommand;
//import io.joffrey.ccpp.projectplanning.domain.event.BudgetItemAdded;
//import io.joffrey.ccpp.projectplanning.domain.event.ProjectBudgetCapExceeded;
//import io.joffrey.ccpp.projectplanning.domain.event.ProjectCreated;
//import io.joffrey.ccpp.projectplanning.domain.event.ProjectMarkedAsReady;
//import io.joffrey.ccpp.projectplanning.domain.exception.CannotModifyReadyProjectException;
//import io.joffrey.ccpp.projectplanning.domain.exception.InvalidProjectDataException;
//import io.joffrey.ccpp.projectplanning.domain.valueobject.BudgetItemId;
//import io.joffrey.ccpp.projectplanning.infrastructure.event.InMemoryEventStore;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//
//import java.math.BigDecimal;
//import java.time.LocalDate;
//import java.util.Currency;
//import java.util.UUID;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.assertj.core.api.Assertions.assertThatThrownBy;
//
//class AddBudgetItemHandlerTest {
//
//    private InMemoryEventStore eventStore;
//    private AddBudgetItemHandler handler;
//
//    private WorkspaceId workspaceId;
//    private UserId userId;
//    private ProjectId projectId;
//    private DateRange timeline;
//    private String title;
//    private String description;
//    private BigDecimal projectBudgetLimit;
//
//    @BeforeEach
//    void setUp() {
//        eventStore = new InMemoryEventStore();
//        handler = new AddBudgetItemHandler(eventStore);
//
//        workspaceId = new WorkspaceId(UUID.randomUUID());
//        userId = new UserId(UUID.randomUUID());
//        projectId = new ProjectId(UUID.randomUUID());
//        timeline = new DateRange(LocalDate.of(2025, 1, 1), LocalDate.of(2025, 3, 31));
//        title = "Q1 Video Series";
//        description = "Educational content";
//        projectBudgetLimit = BigDecimal.valueOf(1000);
//    }
//
//    @Test
//    void should_add_budget_item_to_project() {
//        // GIVEN - project exists
//        var projectCreatedEvent = new ProjectCreated(
//                projectId,
//                workspaceId,
//                userId,
//                title,
//                description,
//                timeline,
//                projectBudgetLimit,
//                0
//        );
//        eventStore.append(projectId.value(), java.util.List.of(projectCreatedEvent), -1);
//
//        var budgetItemId = new BudgetItemId(UUID.randomUUID());
//        var amount = new Money(BigDecimal.valueOf(300), Currency.getInstance("USD"));
//        var command = new AddBudgetItemCommand(projectId, budgetItemId, "Hotel 2 nights", amount);
//
//        // WHEN
//        handler.handle(command);
//
//        // THEN
//        assertThat(eventStore.readStream(projectId.value()))
//                .hasSize(2)
//                .satisfies(events -> {
//                    assertThat(events.get(1)).isInstanceOf(BudgetItemAdded.class);
//                    var budgetAddedEvent = (BudgetItemAdded) events.get(1);
//                    assertThat(budgetAddedEvent.projectId()).isEqualTo(projectId);
//                    assertThat(budgetAddedEvent.budgetItemId()).isEqualTo(budgetItemId);
//                    assertThat(budgetAddedEvent.description()).isEqualTo("Hotel 2 nights");
//                    assertThat(budgetAddedEvent.amount()).isEqualTo(amount);
//                });
//    }
//
//    @Test
//    void should_mark_project_budget_as_over_limit_when_total_budget_exceeds_cap() {
//        // GIVEN - project with small budget limit and one item already added
//        var projectCreatedEvent = new ProjectCreated(
//                projectId,
//                workspaceId,
//                userId,
//                title,
//                description,
//                timeline,
//                new BigDecimal(100)  // small limit
//        );
//        var firstBudgetItem = new BudgetItemAdded(
//                projectId,
//                new BudgetItemId(UUID.randomUUID()),
//                "Item 1",
//                new Money(BigDecimal.valueOf(50), Currency.getInstance("USD"))
//        );
//
//        eventStore.append(
//                projectId.value(),
//                java.util.List.of(projectCreatedEvent, firstBudgetItem),
//                -1
//        );
//
//        var budgetItemId = new BudgetItemId(UUID.randomUUID());
//        var amount = new Money(BigDecimal.valueOf(51), Currency.getInstance("USD"));
//        var command = new AddBudgetItemCommand(projectId, budgetItemId, "Item 2", amount);
//
//        // WHEN
//        handler.handle(command);
//
//        // THEN - should produce both BudgetItemAdded and ProjectBudgetCapExceeded
//        assertThat(eventStore.readStream(projectId.value()))
//                .hasSize(4)  // ProjectCreated + BudgetItemAdded + BudgetItemAdded + ProjectBudgetCapExceeded
//                .satisfies(events -> {
//                    assertThat(events.get(2)).isInstanceOf(BudgetItemAdded.class);
//                    assertThat(events.get(3)).isInstanceOf(ProjectBudgetCapExceeded.class);
//                    var budgetCapExceededEvent = (ProjectBudgetCapExceeded) events.get(3);
//                    assertThat(budgetCapExceededEvent.projectId()).isEqualTo(projectId);
//                    assertThat(budgetCapExceededEvent.actualBudget().amount()).isEqualByComparingTo(BigDecimal.valueOf(101));
//                });
//    }
//
//    @Test
//    void should_prevent_adding_budget_item_when_ready() {
//        // GIVEN - project is marked as READY
//        var projectCreatedEvent = new ProjectCreated(
//                projectId,
//                workspaceId,
//                userId,
//                title,
//                description,
//                timeline,
//                projectBudgetLimit
//        );
//        var projectMarkedAsReadyEvent = new ProjectMarkedAsReady(projectId, workspaceId, userId);
//
//        eventStore.append(
//                projectId.value(),
//                java.util.List.of(projectCreatedEvent, projectMarkedAsReadyEvent),
//                -1
//        );
//
//        var budgetItemId = new BudgetItemId(UUID.randomUUID());
//        var amount = new Money(BigDecimal.valueOf(300), Currency.getInstance("USD"));
//        var command = new AddBudgetItemCommand(projectId, budgetItemId, "Hotel", amount);
//
//        // WHEN & THEN
//        assertThatThrownBy(() -> handler.handle(command))
//                .isInstanceOf(CannotModifyReadyProjectException.class)
//                .hasMessageContaining("Cannot modify project in READY status");
//
//        assertThat(eventStore.readStream(projectId.value())).hasSize(2);
//    }
//
//    @Test
//    void should_reject_empty_budget_item_description() {
//        // GIVEN - project exists
//        var projectCreatedEvent = new ProjectCreated(
//                projectId,
//                workspaceId,
//                userId,
//                title,
//                description,
//                timeline,
//                projectBudgetLimit
//        );
//        eventStore.append(projectId.value(), java.util.List.of(projectCreatedEvent), -1);
//
//        var budgetItemId = new BudgetItemId(UUID.randomUUID());
//        var amount = new Money(BigDecimal.valueOf(300), Currency.getInstance("USD"));
//        var command = new AddBudgetItemCommand(projectId, budgetItemId, "", amount);  // empty description
//
//        // WHEN & THEN
//        assertThatThrownBy(() -> handler.handle(command))
//                .isInstanceOf(InvalidProjectDataException.class)
//                .hasMessageContaining("Budget item description cannot be empty");
//
//        assertThat(eventStore.readStream(projectId.value())).hasSize(1);
//    }
//
//    @Test
//    void should_fail_to_add_budgetItem_in_different_currency() {
//        // GIVEN - project with USD budget item already added
//        var projectCreatedEvent = new ProjectCreated(
//                projectId,
//                workspaceId,
//                userId,
//                title,
//                description,
//                timeline,
//                projectBudgetLimit
//        );
//        var firstBudgetItem = new BudgetItemAdded(
//                projectId,
//                new BudgetItemId(UUID.randomUUID()),
//                "Item in USD",
//                new Money(BigDecimal.valueOf(100), Currency.getInstance("USD"))
//        );
//
//        eventStore.append(
//                projectId.value(),
//                java.util.List.of(projectCreatedEvent, firstBudgetItem),
//                -1
//        );
//
//        var budgetItemId = new BudgetItemId(UUID.randomUUID());
//        var amount = new Money(BigDecimal.valueOf(100), Currency.getInstance("EUR"));  // different currency
//        var command = new AddBudgetItemCommand(projectId, budgetItemId, "Item in EUR", amount);
//
//        // WHEN & THEN
//        assertThatThrownBy(() -> handler.handle(command))
//                .isInstanceOf(CurrencyException.class)
//                .hasMessageContaining("Cannot add budget items with different currencies");
//
//        assertThat(eventStore.readStream(projectId.value())).hasSize(2);
//    }
//}
