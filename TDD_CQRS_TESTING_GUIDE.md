# TDD & CQRS Event Sourcing Testing Guide

This guide explains the testing patterns used in `CreateProjectHandlerTest` and how to apply them to all command handlers.

## Core Principles

### 1. Test via Command Handler (NOT Aggregate)

❌ **WRONG - Direct aggregate testing:**
```java
@Test
void test_project_creation() {
    var project = Project.create(...);
    assertThat(project.getStatus()).isEqualTo(PLANNING);
}
```

**Problems:**
- Tests implementation details (internal state)
- Breaks easily on refactoring
- Duplicates handler tests
- Tests never change as aggregate evolves

✅ **CORRECT - Test via handler:**
```java
@Test
void should_create_project_and_persist_event() {
    var command = new CreateProjectCommand(...);
    handler.handle(command);

    assertThat(eventStore.readStream(projectId.value()))
        .hasSize(1)
        .element(0)
        .isInstanceOf(ProjectCreated.class);
}
```

**Benefits:**
- Tests the actual use case (command → events)
- Survives aggregate refactoring
- Tests through the boundary (application layer)
- Single source of truth for behavior

---

## Test Structure: Given-When-Then

```java
@Test
void should_create_project_and_persist_event() {
    // GIVEN - Setup initial state
    var command = new CreateProjectCommand(...);

    // WHEN - Execute the behavior
    handler.handle(command);

    // THEN - Verify expected events were persisted
    assertThat(eventStore.readStream(projectId.value()))
        .hasSize(1)
        .element(0)
        .isInstanceOf(ProjectCreated.class);
}
```

### GIVEN (Setup)
- Create test data (commands, IDs, values)
- Setup fixture with in-memory event store
- No events should exist yet

### WHEN (Action)
- Call `handler.handle(command)`
- Only ONE action per test
- Command validation and business logic execute here

### THEN (Assertion)
- Read events from `eventStore.readStream(aggregateId)`
- Assert event type, count, and content
- Verify metadata (aggregateId, version, occurredOn)
- NO state assertions - only event assertions

---

## Test Organization: @Nested Classes

```java
class CreateProjectHandlerTest {

    @Nested
    class WhenCreatingValidProject {
        // Happy path tests
    }

    @Nested
    class WhenValidatingProjectData {
        // Validation failure tests
    }

    @Nested
    class EventSourcedBehavior {
        // Event sourcing pattern tests
    }
}
```

**Benefits:**
- Clear test organization by context
- Test class hierarchy in IDE
- Readable test output

---

## Test Patterns

### Pattern 1: Happy Path - Event Creation

Test that a valid command produces the expected event.

```java
@Test
void should_create_project_and_persist_event() {
    var command = new CreateProjectCommand(
        workspaceId, userId, projectId,
        "Valid Title", "Valid Description",
        timeline, budget
    );

    handler.handle(command);

    assertThat(eventStore.readStream(projectId.value()))
        .hasSize(1)
        .element(0)
        .isInstanceOf(ProjectCreated.class)
        .satisfies(event -> {
            var created = (ProjectCreated) event;
            assertThat(created).isEqualTo(
                new ProjectCreated(workspaceId, userId, projectId,
                    "Valid Title", "Valid Description", timeline, budget)
            );
            // Verify metadata
            assertThat(created.getAggregateId()).isEqualTo(projectId.value());
            assertThat(created.getVersion()).isZero();
            assertThat(created.getOccurredOn()).isNotNull();
        });
}
```

### Pattern 2: Validation Failures

Test that invalid data is rejected and NO events are persisted.

```java
@Test
void should_reject_empty_title() {
    var command = new CreateProjectCommand(
        workspaceId, userId, projectId,
        "",  // ← VIOLATION
        validDescription, validTimeline, validBudget
    );

    // WHEN & THEN - Command fails, exception thrown
    assertThatThrownBy(() -> handler.handle(command))
        .isInstanceOf(InvalidProjectDataException.class)
        .hasMessageContaining("Title cannot be empty");

    // Verify NO events were persisted
    assertThat(eventStore.readStream(projectId.value()))
        .isEmpty();
}
```

**Key points:**
- Test ONE validation rule per test
- Assert the exception type and message
- Verify stream is EMPTY (no events created)
- Comment the violation clearly

### Pattern 3: Multiple Events from One Command

Test that a command can produce multiple events (e.g., action + domain event).

```java
@Test
void should_mark_project_budget_as_over_limit_when_total_budget_exceeds_cap() {
    // GIVEN - Project with budget limit exceeded
    var projectCreated = new ProjectCreated(...);
    var budgetItem = new BudgetItemAdded(
        projectId, itemId, "Expensive Item",
        new Money(BigDecimal.valueOf(500), USD)
    );
    eventStore.append(projectId.value(),
        List.of(projectCreated, budgetItem), -1);

    var command = new AddBudgetItemCommand(
        projectId, newItemId, "Another Expensive Item",
        new Money(BigDecimal.valueOf(600), USD)  // ← Exceeds limit
    );

    // WHEN
    handler.handle(command);

    // THEN - Two events: BudgetItemAdded + ProjectBudgetCapExceeded
    assertThat(eventStore.readStream(projectId.value()))
        .hasSize(4)  // ProjectCreated + BudgetItemAdded + BudgetItemAdded + ProjectBudgetCapExceeded
        .satisfies(events -> {
            assertThat(events.get(2))
                .isInstanceOf(BudgetItemAdded.class);
            assertThat(events.get(3))
                .isInstanceOf(ProjectBudgetCapExceeded.class);
        });
}
```

### Pattern 4: Event Sourcing - Reconstruction

Test that loading from history and executing commands produces correct versions.

```java
@Test
void should_persist_events_with_correct_version() {
    // GIVEN - Create first project
    var projectId1 = new ProjectId(UUID.randomUUID());
    handler.handle(new CreateProjectCommand(
        workspaceId, userId, projectId1,
        "Project 1", "First", timeline, budget
    ));

    // WHEN - Create second project
    var projectId2 = new ProjectId(UUID.randomUUID());
    handler.handle(new CreateProjectCommand(
        workspaceId, userId, projectId2,
        "Project 2", "Second", timeline, budget
    ));

    // THEN - Each stream has independent versions
    assertThat(eventStore.readStream(projectId1.value()))
        .element(0)
        .extracting("version")
        .isEqualTo(0);

    assertThat(eventStore.readStream(projectId2.value()))
        .element(0)
        .extracting("version")
        .isEqualTo(0);
}
```

---

## Event Assertion Best Practices

### ✅ Correct: Assert event equality

```java
assertThat(eventStore.readStream(aggregateId))
    .hasSize(1)
    .element(0)
    .isInstanceOf(ProjectCreated.class)
    .satisfies(event -> {
        var created = (ProjectCreated) event;
        assertThat(created).isEqualTo(
            new ProjectCreated(ws, user, proj, title, desc, timeline, budget)
        );
    });
```

### ✅ Correct: Assert metadata presence

```java
.satisfies(event -> {
    var created = (ProjectCreated) event;
    assertThat(created.getAggregateId())
        .isEqualTo(projectId.value());
    assertThat(created.getVersion())
        .isZero();
    assertThat(created.getOccurredOn())
        .isNotNull();
});
```

### ❌ Wrong: Asserting aggregate state

```java
// DON'T DO THIS
var project = Project.loadFromHistory(events);
assertThat(project.getStatus()).isEqualTo(PLANNING);  // ← Testing state, not events
```

### ❌ Wrong: Asserting private fields

```java
// DON'T DO THIS
assertThat(project).hasFieldOrPropertyWithValue("status", PLANNING);
```

---

## Metadata Verification Checklist

For each event assertion, verify:

- ✅ **aggregateId**: Must match the stream ID (command's aggregate ID)
- ✅ **version**: Incremental (0, 1, 2...) within the stream
- ✅ **occurredOn**: Not null, is a valid Instant
- ✅ **event data**: Matches command data exactly

---

## Common Test Mistakes

### ❌ Mistake 1: Testing aggregate directly

```java
// WRONG
@Test
void test_create_project() {
    var project = Project.create(...);
    assertThat(project.getStatus()).isEqualTo(PLANNING);
}
```

**Fix:** Test via handler and verify events instead.

### ❌ Mistake 2: Multiple concerns per test

```java
// WRONG - Tests both creation AND budget checking
@Test
void should_create_and_manage_budget() {
    handler.handle(createCommand);
    handler.handle(addBudgetCommand);
    assertThat(eventStore.readStream(id)).hasSize(3);
}
```

**Fix:** One test = one behavior concern.

### ❌ Mistake 3: Not verifying failure state

```java
// WRONG - Doesn't check that NO events were persisted
@Test
void should_reject_empty_title() {
    assertThatThrownBy(() -> handler.handle(invalidCommand))
        .isInstanceOf(InvalidProjectDataException.class);
    // Missing: assertThat(eventStore.readStream(id)).isEmpty();
}
```

**Fix:** Always verify no events on error.

### ❌ Mistake 4: Testing implementation details

```java
// WRONG
@Test
void test_title_validation() {
    var project = new Project(...);
    project.validateTitle(""); // Testing private method
}
```

**Fix:** Call through the public handler interface.

---

## Applying to Other Handlers

For each `XxxHandler`, follow this template:

```java
class XxxHandlerTest {

    private EventStore eventStore;
    private XxxHandler handler;

    @BeforeEach
    void setUp() {
        eventStore = new InMemoryEventStore();
        handler = new XxxHandler(eventStore);
    }

    @Nested
    class WhenExecutingValidCommand {
        @Test
        void should_produce_expected_events() {
            // GIVEN
            var command = new XxxCommand(...);

            // WHEN
            handler.handle(command);

            // THEN
            assertThat(eventStore.readStream(aggregateId))
                .hasSize(expectedCount)
                .element(0)
                .isInstanceOf(ExpectedEvent.class);
        }
    }

    @Nested
    class WhenValidatingData {
        @Test
        void should_reject_invalid_state() {
            var command = new XxxCommand(...withViolation...);

            assertThatThrownBy(() -> handler.handle(command))
                .isInstanceOf(DomainException.class);

            assertThat(eventStore.readStream(aggregateId))
                .isEmpty();
        }
    }
}
```

---

## Key Takeaways

| Aspect | ✅ Do | ❌ Don't |
|--------|------|---------|
| **What to test** | Handler behavior via commands | Aggregate state directly |
| **What to verify** | Events produced | State changes |
| **Test organization** | @Nested by context | Flat test methods |
| **Assertion scope** | One concern per test | Multiple assertions mixing topics |
| **Validation tests** | Verify no events persisted | Only check exception |
| **Metadata** | Always verify aggregateId, version, occurredOn | Ignore metadata |
| **Test isolation** | Each test uses fresh fixture | Shared state between tests |

---

## TDD Workflow (Red-Green-Refactor)

### 🔴 RED: Write failing test first

```java
@Test
void should_create_project_and_persist_event() {
    var command = new CreateProjectCommand(...);
    handler.handle(command);  // ← Fails: handler doesn't exist
    assertThat(eventStore.readStream(id)).hasSize(1);
}
```

### 🟢 GREEN: Write minimal code to pass

```java
public class CreateProjectHandler {
    public void handle(CreateProjectCommand cmd) {
        var project = Project.create(cmd...);
        eventStore.append(cmd.projectId().value(),
            project.uncommittedEvents(), -1);
    }
}
```

### 🔵 REFACTOR: Clean up without changing behavior

```java
// Extract validation, improve readability, etc.
// Tests still pass - behavior unchanged
```

---

This is production-ready CQRS testing! 🎯
