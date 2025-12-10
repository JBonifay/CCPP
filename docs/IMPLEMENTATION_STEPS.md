# CCPP - Step-by-Step Implementation Guide

This document provides a detailed, actionable roadmap for implementing the CCPP platform following Clean Architecture, DDD, Event Sourcing, CQRS, and TDD principles.

---

## Phase 1: Foundation + Project Core (Week 1-2)

### Goal
Establish project structure with Clean Architecture and implement the core Project aggregate using TDD.

### Step 1.1: Setup Maven Multi-Module Project

- [x] Create root directory and initialize git repository
```bash
mkdir ccpp
cd ccpp
git init
```

- [x] Create parent `pom.xml` with multi-module configuration
```bash
touch pom.xml
```

**Parent POM should include:**
- Java 25 configuration
- Spring Boot parent dependency
- Module definitions: `shared`, `ApiGateway`, `ProjectPlanning`, `Workspace`, `Ideation`, `Notification`
- Dependency management for common libraries

- [x] Create module directories
```bash
mkdir -p shared/src/{main,test}/java
mkdir -p ApiGateway/src/{main,test}/java
mkdir -p ProjectPlanning/src/{main,test}/java
mkdir -p Workspace/src/{main,test}/java
mkdir -p Ideation/src/{main,test}/java
mkdir -p Notification/src/{main,test}/java
```

- [x] Create module-specific `pom.xml` files for each module
- [x] Verify build works: `mvn clean install`

### Step 1.2: Setup Shared Module (Shared Kernel)

**Directory structure:**
```
shared/
  src/main/java/com/ccpp/shared/
    domain/
      AggregateRoot.java
      Entity.java
      ValueObject.java
      DomainEvent.java
      EventStore.java (interface)
    valueobjects/
      Money.java
      DateRange.java
      Email.java
    identities/
      WorkspaceId.java
      UserId.java
```

- [x] **Create base `AggregateRoot` class**
  - List of uncommitted events
  - Methods: `addEvent()`, `clearEvents()`, `getEvents()`
  - Abstract method: `getId()`

- [x] **Create `DomainEvent` interface**
  - `getEventId()`
  - `getEventType()`
  - `getAggregateId()`
  - `getWorkspaceId()`
  - `getTimestamp()`
  - `getVersion()`

- [x] **Create `EventStore` interface (port)**
```java
public interface EventStore {
    void append(String streamId, List<DomainEvent> events, int expectedVersion);
    List<DomainEvent> readStream(String streamId);
    List<DomainEvent> readAllEvents();
}
```

- [x] **Create Value Objects with TDD**

**Money Value Object:**
- [x] Write test: `should_create_money_with_amount_and_currency()`
- [x] Write test: `should_add_two_money_amounts()`
- [x] Write test: `should_throw_exception_when_adding_different_currencies()`
- [x] Implement `Money` class
  - Fields: `BigDecimal amount`, `String currency`
  - Methods: `add()`, `subtract()`, `multiply()`
  - Immutable
  - Value equality (equals/hashCode)

**DateRange Value Object:**
- [x] Write test: `should_create_valid_date_range()`
- [x] Write test: `should_throw_exception_when_end_before_start()`
- [x] Implement `DateRange` class
  - Fields: `LocalDate startDate`, `LocalDate endDate`
  - Immutable

**Email Value Object:**
- [x] Write test: `should_create_valid_email()`
- [x] Write test: `should_throw_exception_for_invalid_email()`
- [x] Implement `Email` class
  - Validation in constructor
  - Immutable

- [x] **Create Identity Value Objects**
  - `WorkspaceId`: UUID-based identity
  - `UserId`: UUID-based identity
  - `ProjectId`: UUID-based identity
  - All should be immutable with value equality

- [x] Run tests: `mvn test`
- [x] Verify all shared module tests pass

### Step 1.3: Implement Project Aggregate (TDD)

**Module:** `ProjectPlanning/`

**Directory structure:**
```
ProjectPlanning/
  src/main/java/com/ccpp/projectplanning/
    domain/
      model/
        Project.java (Aggregate Root)
        BudgetItem.java (Entity)
        Participant.java (Entity)
        Note.java (Value Object)
        Status.java (Enum)
        InvitationStatus.java (Enum)
      events/
        ProjectCreated.java
        ProjectMarkedAsReady.java
        ProjectTimelineChanged.java
        BudgetItemAdded.java
        BudgetItemUpdated.java
        BudgetItemRemoved.java
        NoteAdded.java
        ParticipantInvited.java
        ParticipantAccepted.java
        ParticipantDeclined.java
      repository/
        ProjectRepository.java (interface)
      exceptions/
        ProjectAlreadyReadyException.java
        CannotModifyReadyProjectException.java
        InvalidTimelineException.java
```

#### **Test 1: Create Project**

- [ ] Write test: `should_create_project_with_valid_data()`
```java
@Test
void should_create_project_with_valid_data() {
    // Given
    var workspaceId = new WorkspaceId(UUID.randomUUID());
    var timeline = new DateRange(LocalDate.of(2025, 1, 1), LocalDate.of(2025, 3, 31));
    var userId = new UserId(UUID.randomUUID());

    // When
    var project = Project.create(workspaceId, "Q1 Video Series", "Educational content", timeline, userId);

    // Then
    assertThat(project.getStatus()).isEqualTo(Status.PLANNING);
    assertThat(project.getTitle()).isEqualTo("Q1 Video Series");
    assertThat(project.getWorkspaceId()).isEqualTo(workspaceId);
    assertThat(project.getEvents()).hasSize(1).first().isInstanceOf(ProjectCreated.class);
}
```

- [ ] Implement minimum code to make test pass:
  - Create `Status` enum (PLANNING, READY)
  - Create `Project` class extending `AggregateRoot`
  - Implement static factory method `create()`
  - Create `ProjectCreated` event

- [ ] Run test: should be GREEN

#### **Test 2: Add Budget Items**

- [ ] Write test: `should_add_budget_item_to_project()`
- [ ] Write test: `should_calculate_total_budget_from_items()`
```java
@Test
void should_calculate_total_budget_from_items() {
    // Given
    var project = createPlanningProject();

    // When
    project.addBudgetItem("Hotel", Money.of(300, "USD"));
    project.addBudgetItem("Equipment", Money.of(150, "USD"));

    // Then
    assertThat(project.getTotalBudget()).isEqualTo(Money.of(450, "USD"));
}
```

- [ ] Implement `BudgetItem` entity
  - Fields: `BudgetItemId`, `description`, `amount`, `category`
- [ ] Implement `addBudgetItem()` method on Project
- [ ] Implement `getTotalBudget()` method
- [ ] Create `BudgetItemAdded` event
- [ ] Run tests: should be GREEN

#### **Test 3: Mark Project as READY**

- [ ] Write test: `should_mark_project_as_ready()`
- [ ] Write test: `should_emit_project_marked_as_ready_event()`
- [ ] Write test: `should_throw_exception_when_marking_already_ready_project()`

- [ ] Implement `markAsReady()` method
  - Check current status
  - Throw exception if already READY
  - Change status to READY
  - Emit `ProjectMarkedAsReady` event
- [ ] Run tests: should be GREEN

#### **Test 4: Timeline Changes (Immutability)**

- [ ] Write test: `should_change_timeline_when_planning()`
- [ ] Write test: `should_prevent_timeline_change_when_ready()`
```java
@Test
void should_prevent_modifications_when_ready() {
    // Given
    var project = createPlanningProject();
    project.markAsReady(userId);
    var newTimeline = new DateRange(LocalDate.of(2025, 2, 1), LocalDate.of(2025, 4, 30));

    // When / Then
    assertThatThrownBy(() -> project.changeTimeline(newTimeline))
            .isInstanceOf(CannotModifyReadyProjectException.class);
}
```

- [ ] Implement `changeTimeline()` method
  - Check status (throw if READY)
  - Validate timeline
  - Update timeline
  - Emit `ProjectTimelineChanged` event
- [ ] Create exception classes
- [ ] Run tests: should be GREEN

#### **Test 5: Notes**

- [ ] Write test: `should_add_note_to_project()`
- [ ] Create `Note` value object (content, createdAt, createdBy)
- [ ] Implement `addNote()` method
- [ ] Create `NoteAdded` event
- [ ] Run tests: should be GREEN

#### **Test 6: Budget Item Operations**

- [ ] Write test: `should_update_budget_item()`
- [ ] Write test: `should_remove_budget_item()`
- [ ] Write test: `should_throw_exception_when_updating_nonexistent_budget_item()`
- [ ] Implement `updateBudgetItem()` method
- [ ] Implement `removeBudgetItem()` method
- [ ] Create corresponding events
- [ ] Run tests: should be GREEN

#### **Test 7-10: Participants (defer to Phase 4)**

Mark as TODO for Phase 4.

- [ ] Verify total test count: 15+ tests
- [ ] Verify domain test coverage: 95%+
- [ ] Run full test suite: `mvn test`

### Step 1.4: Implement In-Memory Event Store

**Module:** `ProjectPlanning/src/main/java/com/ccpp/projectplanning/infrastructure/`

- [ ] Create `InMemoryEventStore` implementing `EventStore` interface
```java
public class InMemoryEventStore implements EventStore {
    private final Map<String, List<DomainEvent>> streams = new ConcurrentHashMap<>();

    @Override
    public void append(String streamId, List<DomainEvent> events, int expectedVersion) {
        // Implement optimistic concurrency check
        // Append events to stream
    }

    @Override
    public List<DomainEvent> readStream(String streamId) {
        return streams.getOrDefault(streamId, List.of());
    }
}
```

- [ ] Write tests for `InMemoryEventStore`
  - Test: `should_append_events_to_stream()`
  - Test: `should_read_events_from_stream()`
  - Test: `should_throw_exception_on_version_conflict()`

- [ ] Create `ProjectRepository` interface (port)
```java
public interface ProjectRepository {
    void save(Project project);
    Optional<Project> findById(ProjectId projectId);
    List<Project> findByWorkspaceId(WorkspaceId workspaceId);
}
```

- [ ] Create `EventSourcedProjectRepository` implementation
  - Save: Get events from aggregate, append to event store
  - Find: Load events from event store, replay to rebuild aggregate
  - Implement event replay logic

- [ ] Write tests for `EventSourcedProjectRepository`
- [ ] Run tests: all should pass

### Step 1.5: Phase 1 Completion Checklist

- [ ] All domain tests passing (15+ tests)
- [ ] Test coverage: 95%+ on domain layer
- [ ] Event store successfully stores and retrieves events
- [ ] Can create, modify, and mark projects as READY
- [ ] Budget calculation works correctly
- [ ] Timeline immutability enforced
- [ ] All code follows Clean Architecture principles
- [ ] No framework dependencies in domain layer
- [ ] Run full build: `mvn clean verify`
- [ ] Git commit: "Phase 1 complete: Project aggregate with event sourcing"

---

## Phase 2: CQRS + Projections (Week 3)

### Goal
Implement CQRS pattern with command handlers (write side) and query handlers (read side) with projections.

### Step 2.1: Application Layer Structure

**Directory structure:**
```
ProjectPlanning/
  src/main/java/com/ccpp/projectplanning/
    application/
      commands/
        CreateProjectCommand.java
        AddBudgetItemCommand.java
        UpdateBudgetItemCommand.java
        RemoveBudgetItemCommand.java
        MarkProjectAsReadyCommand.java
        ChangeProjectTimelineCommand.java
        AddNoteCommand.java
      commandhandlers/
        CreateProjectCommandHandler.java
        AddBudgetItemCommandHandler.java
        MarkProjectAsReadyCommandHandler.java
        ...
      queries/
        GetProjectDetailsQuery.java
        GetProjectListQuery.java
      queryhandlers/
        GetProjectDetailsQueryHandler.java
        GetProjectListQueryHandler.java
      dto/
        ProjectListDTO.java
        ProjectDetailDTO.java
        BudgetItemDTO.java
```

### Step 2.2: Implement Commands (Write Side)

#### **CreateProjectCommand**

- [ ] Create `CreateProjectCommand` class
```java
public record CreateProjectCommand(
    WorkspaceId workspaceId,
    String title,
    String description,
    DateRange timeline,
    UserId userId
) {}
```

- [ ] Write test for `CreateProjectCommandHandler`
```java
@Test
void should_create_project_when_handling_command() {
    // Given
    var command = new CreateProjectCommand(workspaceId, "Title", "Desc", timeline, userId);
    var handler = new CreateProjectCommandHandler(projectRepository, eventBus);

    // When
    var projectId = handler.handle(command);

    // Then
    var project = projectRepository.findById(projectId);
    assertThat(project).isPresent();
    assertThat(project.get().getTitle()).isEqualTo("Title");
}
```

- [ ] Implement `CreateProjectCommandHandler`
  - Accept command
  - Call `Project.create()` factory method
  - Save project via repository
  - Publish events to event bus
  - Return ProjectId

- [ ] Run test: should be GREEN

#### **AddBudgetItemCommand**

- [ ] Create `AddBudgetItemCommand`
- [ ] Write test for handler
- [ ] Implement `AddBudgetItemCommandHandler`
  - Load project from repository
  - Call `project.addBudgetItem()`
  - Save project
  - Publish events
- [ ] Run test: should be GREEN

#### **MarkProjectAsReadyCommand**

- [ ] Create `MarkProjectAsReadyCommand`
- [ ] Write test for handler
- [ ] Implement `MarkProjectAsReadyCommandHandler`
- [ ] Run test: should be GREEN

- [ ] Implement remaining command handlers following same pattern:
  - [ ] `UpdateBudgetItemCommandHandler`
  - [ ] `RemoveBudgetItemCommandHandler`
  - [ ] `ChangeProjectTimelineCommandHandler`
  - [ ] `AddNoteCommandHandler`

### Step 2.3: Implement Event Bus

- [ ] Create `EventBus` interface
```java
public interface EventBus {
    void publish(DomainEvent event);
    void subscribe(String eventType, EventHandler handler);
}
```

- [ ] Create `InMemoryEventBus` implementation
```java
public class InMemoryEventBus implements EventBus {
    private final Map<String, List<EventHandler>> handlers = new ConcurrentHashMap<>();

    @Override
    public void publish(DomainEvent event) {
        var eventHandlers = handlers.getOrDefault(event.getEventType(), List.of());
        eventHandlers.forEach(handler -> handler.handle(event));
    }
}
```

- [ ] Write tests for `InMemoryEventBus`

### Step 2.4: Implement Projections (Read Side)

**Directory structure:**
```
ProjectPlanning/
  src/main/java/com/ccpp/projectplanning/
    infrastructure/
      projections/
        ProjectListProjection.java
        ProjectDetailProjection.java
      readmodel/
        ProjectListReadModel.java (in-memory storage)
        ProjectDetailReadModel.java
```

#### **ProjectListView**

- [ ] Create `ProjectListDTO`
```java
public record ProjectListDTO(
    String projectId,
    String workspaceId,
    String title,
    String status,
    BigDecimal totalBudget,
    int participantCount,
    LocalDateTime createdAt
) {}
```

- [ ] Create `ProjectListReadModel` interface (port)
```java
public interface ProjectListReadModel {
    void add(ProjectListDTO dto);
    void update(String projectId, ProjectListDTO dto);
    void remove(String projectId);
    List<ProjectListDTO> findByWorkspaceId(String workspaceId);
}
```

- [ ] Create `InMemoryProjectListReadModel` implementation
```java
public class InMemoryProjectListReadModel implements ProjectListReadModel {
    private final Map<String, ProjectListDTO> projects = new ConcurrentHashMap<>();

    // Implementation...
}
```

- [ ] Create `ProjectListProjection` (event handler)
```java
public class ProjectListProjection implements EventHandler {
    private final ProjectListReadModel readModel;

    @Override
    public void handle(DomainEvent event) {
        if (event instanceof ProjectCreated e) {
            handleProjectCreated(e);
        } else if (event instanceof BudgetItemAdded e) {
            handleBudgetItemAdded(e);
        }
        // Handle other events...
    }

    private void handleProjectCreated(ProjectCreated event) {
        var dto = new ProjectListDTO(
            event.getProjectId(),
            event.getWorkspaceId(),
            event.getTitle(),
            "PLANNING",
            BigDecimal.ZERO,
            0,
            event.getTimestamp()
        );
        readModel.add(dto);
    }
}
```

- [ ] Write tests for `ProjectListProjection`
  - Test: `should_add_project_when_project_created_event_received()`
  - Test: `should_update_budget_when_budget_item_added()`
  - Test: `should_update_status_when_marked_as_ready()`

#### **ProjectDetailView**

- [ ] Create `ProjectDetailDTO` (with embedded budget items, notes, participants)
- [ ] Create `ProjectDetailReadModel` interface
- [ ] Create `InMemoryProjectDetailReadModel`
- [ ] Create `ProjectDetailProjection`
- [ ] Write tests for projection

### Step 2.5: Implement Query Handlers

#### **GetProjectListQuery**

- [ ] Create `GetProjectListQuery`
```java
public record GetProjectListQuery(WorkspaceId workspaceId) {}
```

- [ ] Create `GetProjectListQueryHandler`
```java
public class GetProjectListQueryHandler {
    private final ProjectListReadModel readModel;

    public List<ProjectListDTO> handle(GetProjectListQuery query) {
        return readModel.findByWorkspaceId(query.workspaceId().toString());
    }
}
```

- [ ] Write integration test
```java
@Test
void should_return_projects_for_workspace() {
    // Given: Create project via command handler (triggers projection)
    commandHandler.handle(new CreateProjectCommand(...));

    // When: Query for projects
    var query = new GetProjectListQuery(workspaceId);
    var results = queryHandler.handle(query);

    // Then
    assertThat(results).hasSize(1);
    assertThat(results.get(0).title()).isEqualTo("Q1 Video Series");
}
```

#### **GetProjectDetailsQuery**

- [ ] Create `GetProjectDetailsQuery`
- [ ] Create `GetProjectDetailsQueryHandler`
- [ ] Write integration test

### Step 2.6: Wire Everything Together

- [ ] Create application configuration class
- [ ] Wire up event bus subscriptions
- [ ] Subscribe projections to events
```java
eventBus.subscribe("ProjectCreated", projectListProjection);
eventBus.subscribe("ProjectCreated", projectDetailProjection);
eventBus.subscribe("BudgetItemAdded", projectListProjection);
// etc...
```

- [ ] Test end-to-end flow:
  1. Command → Command Handler
  2. Domain logic + events
  3. Save to event store
  4. Publish events
  5. Projections update read models
  6. Query returns updated data

### Step 2.7: Phase 2 Completion Checklist

- [ ] All command handlers implemented and tested
- [ ] All query handlers implemented and tested
- [ ] Projections update correctly from events
- [ ] Integration tests pass (command → event → projection → query)
- [ ] Read models separated from write models
- [ ] Event bus working correctly
- [ ] Test coverage: 85%+ on application layer
- [ ] Run full build: `mvn clean verify`
- [ ] Git commit: "Phase 2 complete: CQRS with projections"

---

## Phase 3: Workspace + Multi-Tenancy (Week 4)

### Goal
Implement Workspace bounded context with subscription limits and enforce multi-tenant isolation.

### Step 3.1: Implement Workspace Aggregate

**Module:** `Workspace/`

**Directory structure:**
```
Workspace/
  src/main/java/com/ccpp/workspace/
    domain/
      model/
        Workspace.java (Aggregate Root)
        SubscriptionTier.java (Enum)
        Member.java (Entity)
      events/
        WorkspaceCreated.java
        WorkspaceSubscriptionUpgraded.java
        WorkspaceMemberAdded.java
        WorkspaceProjectCreationApproved.java
        WorkspaceProjectLimitReached.java
      repository/
        WorkspaceRepository.java
```

#### **Test 1: Create Workspace**

- [ ] Write test: `should_create_workspace_with_freemium_tier()`
- [ ] Create `SubscriptionTier` enum (FREEMIUM, PREMIUM)
- [ ] Implement `Workspace` aggregate
- [ ] Create `WorkspaceCreated` event
- [ ] Run test: should be GREEN

#### **Test 2: Subscription Limits**

- [ ] Write test: `should_allow_project_creation_when_under_limit()`
- [ ] Write test: `should_reject_project_creation_when_freemium_at_limit()`
- [ ] Write test: `should_allow_unlimited_projects_for_premium()`

- [ ] Implement `approveProjectCreation()` method
```java
public boolean approveProjectCreation(int currentProjectCount) {
    if (subscriptionTier == FREEMIUM && currentProjectCount >= 2) {
        addEvent(new WorkspaceProjectLimitReached(this.id));
        return false;
    }
    addEvent(new WorkspaceProjectCreationApproved(this.id));
    return true;
}
```

- [ ] Run tests: should be GREEN

#### **Test 3: Upgrade Subscription**

- [ ] Write test: `should_upgrade_from_freemium_to_premium()`
- [ ] Implement `upgradeSubscription()` method
- [ ] Create `WorkspaceSubscriptionUpgraded` event
- [ ] Run test: should be GREEN

### Step 3.2: Workspace Read Model (Project Count)

- [ ] Create `WorkspaceProjectCountDTO`
```java
public record WorkspaceProjectCountDTO(
    String workspaceId,
    int projectCount,
    String subscriptionTier,
    int maxProjects
) {}
```

- [ ] Create `WorkspaceProjectCountReadModel` interface
- [ ] Create `InMemoryWorkspaceProjectCountReadModel`
- [ ] Create `WorkspaceProjectCountProjection`
  - Listen to `ProjectCreated` → increment count
  - Listen to `ProjectDeleted` → decrement count (future)
  - Listen to `WorkspaceCreated` → initialize count
  - Listen to `WorkspaceSubscriptionUpgraded` → update tier

- [ ] Write tests for projection

### Step 3.3: Implement Project Creation Saga

**Saga Flow:**
```
1. User → CreateProjectCommand
2. Check workspace subscription limit (via read model)
3. Call Workspace.approveProjectCreation()
4. If approved → Create Project
5. Publish ProjectCreated event
6. WorkspaceProjectCountProjection updates count
```

- [ ] Create `CreateProjectSaga` or enhance `CreateProjectCommandHandler`

```java
public class CreateProjectCommandHandler {
    private final WorkspaceRepository workspaceRepository;
    private final ProjectRepository projectRepository;
    private final WorkspaceProjectCountReadModel countReadModel;
    private final EventBus eventBus;

    public ProjectId handle(CreateProjectCommand command) {
        // 1. Get current project count
        var count = countReadModel.getCount(command.workspaceId());

        // 2. Load workspace aggregate
        var workspace = workspaceRepository.findById(command.workspaceId())
            .orElseThrow();

        // 3. Check if project creation is approved
        var approved = workspace.approveProjectCreation(count);
        if (!approved) {
            throw new ProjectLimitReachedException();
        }

        // 4. Save workspace (with approval event)
        workspaceRepository.save(workspace);

        // 5. Create project
        var project = Project.create(...);
        projectRepository.save(project);

        // 6. Publish all events
        publishEvents(workspace.getEvents());
        publishEvents(project.getEvents());

        return project.getId();
    }
}
```

- [ ] Write integration test
```java
@Test
void should_reject_third_project_for_freemium_workspace() {
    // Given: Freemium workspace with 2 existing projects
    var workspace = createFreemiumWorkspaceWithTwoProjects();

    // When: Try to create third project
    var command = new CreateProjectCommand(workspace.getId(), ...);

    // Then: Should throw exception
    assertThatThrownBy(() -> commandHandler.handle(command))
        .isInstanceOf(ProjectLimitReachedException.class);
}
```

### Step 3.4: Add Tenant Isolation to All Queries

- [ ] Update `ProjectListReadModel.findByWorkspaceId()` - already done
- [ ] Update all command handlers to validate workspaceId
- [ ] Add integration tests for cross-tenant isolation
```java
@Test
void should_not_return_projects_from_other_workspace() {
    // Given: Two workspaces with projects
    var workspace1 = createWorkspace();
    var workspace2 = createWorkspace();
    createProject(workspace1.getId(), "Project 1");
    createProject(workspace2.getId(), "Project 2");

    // When: Query workspace 1 projects
    var results = queryHandler.handle(new GetProjectListQuery(workspace1.getId()));

    // Then: Should only see workspace 1 projects
    assertThat(results).hasSize(1);
    assertThat(results.get(0).title()).isEqualTo("Project 1");
}
```

### Step 3.5: API Gateway Context (Preparation)

- [ ] Create authentication context holder
```java
public class SecurityContext {
    private static final ThreadLocal<AuthContext> context = new ThreadLocal<>();

    public static void setContext(UserId userId, WorkspaceId workspaceId) {
        context.set(new AuthContext(userId, workspaceId));
    }

    public static AuthContext getContext() {
        return context.get();
    }
}
```

- [ ] Update command handlers to use `SecurityContext` for userId and workspaceId
- [ ] Add validation: ensure command's workspaceId matches authenticated user's workspace

### Step 3.6: Phase 3 Completion Checklist

- [ ] Workspace aggregate implemented with TDD
- [ ] Subscription limits enforced (freemium: 2 projects, premium: unlimited)
- [ ] Project creation saga prevents limit violations
- [ ] Workspace project count projection working
- [ ] All queries filtered by workspaceId
- [ ] Cross-tenant isolation tested and verified
- [ ] Security context prepared for API layer
- [ ] Test coverage maintained: 85%+
- [ ] Run full build: `mvn clean verify`
- [ ] Git commit: "Phase 3 complete: Workspace context with multi-tenancy"

---

## Phase 4: Participant Saga (Week 5)

### Goal
Implement participant invitation workflow with email notifications using saga choreography.

### Step 4.1: Add Participant Entity to Project

- [ ] Create `Participant` entity
```java
public class Participant extends Entity {
    private final ParticipantId id;
    private final Email email;
    private final String name;
    private InvitationStatus status; // INVITED, ACCEPTED, DECLINED
    private LocalDateTime responseAt;
}
```

- [ ] Create `InvitationStatus` enum
- [ ] Create participant events:
  - `ParticipantInvited`
  - `ParticipantAccepted`
  - `ParticipantDeclined`

#### **Test: Add Participants**

- [ ] Write test: `should_add_participant_to_project()`
- [ ] Write test: `should_prevent_duplicate_participant_email()`
- [ ] Implement `addParticipant()` method on Project
- [ ] Run tests: should be GREEN

#### **Test: Participant Responses**

- [ ] Write test: `should_record_participant_acceptance()`
- [ ] Write test: `should_record_participant_decline()`
- [ ] Write test: `should_emit_participant_declined_event()`

- [ ] Implement `recordParticipantResponse()` method
```java
public void recordParticipantResponse(Email email, ParticipantResponse response) {
    var participant = findParticipantByEmail(email);
    participant.recordResponse(response);

    if (response == ACCEPTED) {
        addEvent(new ParticipantAccepted(this.id, participant.getId(), email));
    } else {
        addEvent(new ParticipantDeclined(this.id, participant.getId(), email));
    }
}
```

- [ ] Run tests: should be GREEN

### Step 4.2: Update ProjectMarkedAsReady Event

- [ ] Modify `ProjectMarkedAsReady` event to include participant list
```java
public record ProjectMarkedAsReady(
    ProjectId projectId,
    WorkspaceId workspaceId,
    List<ParticipantData> participants,
    UserId markedByUserId,
    LocalDateTime timestamp
) implements DomainEvent {}
```

- [ ] Update `Project.markAsReady()` to include participants in event

### Step 4.3: Implement Notification Context

**Module:** `Notification/`

**Directory structure:**
```
Notification/
  src/main/java/com/ccpp/notification/
    domain/
      model/
        Notification.java (Aggregate Root)
        NotificationType.java (Enum: EMAIL, SMS)
        NotificationStatus.java (Enum)
      events/
        NotificationScheduled.java
        NotificationSent.java
        NotificationFailed.java
      services/
        EmailService.java (interface/port)
```

#### **Notification Aggregate**

- [ ] Write test: `should_create_notification_with_recipients()`
- [ ] Implement `Notification` aggregate
```java
public class Notification extends AggregateRoot {
    private final NotificationId id;
    private final List<String> recipients;
    private final NotificationType type;
    private final String templateId;
    private final Map<String, Object> payload;
    private NotificationStatus status; // PENDING, SENT, FAILED
    private LocalDateTime sentAt;
}
```

- [ ] Create notification events
- [ ] Run tests: should be GREEN

#### **Email Service (Infrastructure)**

- [ ] Create `EmailService` interface
```java
public interface EmailService {
    void sendEmail(String to, String subject, String body);
}
```

- [ ] Create `InMemoryEmailService` (logs to console)
```java
public class InMemoryEmailService implements EmailService {
    @Override
    public void sendEmail(String to, String subject, String body) {
        System.out.println("=== EMAIL ===");
        System.out.println("To: " + to);
        System.out.println("Subject: " + subject);
        System.out.println("Body: " + body);
        System.out.println("=============");
    }
}
```

### Step 4.4: Implement Participant Invitation Saga

**Saga Flow:**
```
1. Project marked as READY
   → ProjectMarkedAsReady event published
2. ParticipantInvitationSaga listens to event
   → For each participant, send invitation email
   → Create Notification aggregate
   → Emit NotificationScheduled
3. NotificationService listens to NotificationScheduled
   → Send email via EmailService
   → Emit NotificationSent
```

- [ ] Create `ParticipantInvitationSaga`
```java
public class ParticipantInvitationSaga implements EventHandler {
    private final NotificationRepository notificationRepository;
    private final EventBus eventBus;

    @Override
    public void handle(DomainEvent event) {
        if (event instanceof ProjectMarkedAsReady e) {
            handleProjectMarkedAsReady(e);
        }
    }

    private void handleProjectMarkedAsReady(ProjectMarkedAsReady event) {
        for (var participant : event.participants()) {
            // Create notification for each participant
            var notification = Notification.create(
                participant.email(),
                NotificationType.EMAIL,
                "participant_invitation",
                Map.of(
                    "projectTitle", event.getProjectTitle(),
                    "participantName", participant.name()
                )
            );

            notificationRepository.save(notification);
            publishEvents(notification.getEvents());
        }
    }
}
```

- [ ] Create `NotificationSendingService`
```java
public class NotificationSendingService implements EventHandler {
    private final EmailService emailService;
    private final NotificationRepository notificationRepository;

    @Override
    public void handle(DomainEvent event) {
        if (event instanceof NotificationScheduled e) {
            sendNotification(e);
        }
    }

    private void sendNotification(NotificationScheduled event) {
        try {
            // Load notification
            var notification = notificationRepository.findById(event.getNotificationId());

            // Send email
            emailService.sendEmail(
                event.getRecipient(),
                "Project Invitation",
                buildEmailBody(event.getPayload())
            );

            // Mark as sent
            notification.markAsSent();
            notificationRepository.save(notification);
            publishEvents(notification.getEvents());

        } catch (Exception ex) {
            // Handle failure
            notification.markAsFailed(ex.getMessage());
            notificationRepository.save(notification);
        }
    }
}
```

### Step 4.5: Wire Up Saga

- [ ] Register saga with event bus
```java
eventBus.subscribe("ProjectMarkedAsReady", participantInvitationSaga);
eventBus.subscribe("NotificationScheduled", notificationSendingService);
```

### Step 4.6: Implement Participant Response Commands

- [ ] Create `AcceptParticipationCommand`
- [ ] Create `DeclineParticipationCommand`
- [ ] Implement command handlers
  - Load project
  - Call `recordParticipantResponse()`
  - Save project
  - Publish events

### Step 4.7: Integration Test for Full Saga

```java
@Test
void should_send_invitations_when_project_marked_ready() {
    // Given: Project with participants
    var project = createProjectWithParticipants("alice@example.com", "bob@example.com");

    // When: Mark as ready
    commandHandler.handle(new MarkProjectAsReadyCommand(project.getId(), userId));

    // Then: Verify notifications sent
    await().atMost(5, SECONDS).until(() -> {
        var notifications = notificationRepository.findAll();
        return notifications.size() == 2 &&
               notifications.stream().allMatch(n -> n.getStatus() == SENT);
    });
}
```

- [ ] Write and run integration test
- [ ] Verify emails logged to console

### Step 4.8: Phase 4 Completion Checklist

- [ ] Participant entity added to Project aggregate
- [ ] Participant invitation saga working end-to-end
- [ ] Notification context implemented
- [ ] Emails sent when project marked as READY
- [ ] Participant response commands working
- [ ] Events properly chained (ProjectMarkedAsReady → NotificationScheduled → NotificationSent)
- [ ] Integration tests pass
- [ ] Test coverage maintained: 85%+
- [ ] Run full build: `mvn clean verify`
- [ ] Git commit: "Phase 4 complete: Participant invitation saga"

---

## Phase 5: Ideation + Brainstorm (Week 6)

### Goal
Implement Ideation bounded context for workspace-level brainstorming with idea-to-project conversion saga.

### Step 5.1: Implement Brainstorm Aggregate

**Module:** `Ideation/`

**Directory structure:**
```
Ideation/
  src/main/java/com/ccpp/ideation/
    domain/
      model/
        Brainstorm.java (Aggregate Root)
        Idea.java (Entity)
        ConversionStatus.java (Enum)
      events/
        BrainstormCreated.java
        IdeaAdded.java
        IdeaUpdated.java
        BrainstormIdeaConverted.java
        BrainstormLinkedToProject.java
      repository/
        BrainstormRepository.java
```

#### **Test: Create Brainstorm**

- [ ] Write test: `should_create_brainstorm_for_workspace()`
- [ ] Implement `Brainstorm` aggregate
```java
public class Brainstorm extends AggregateRoot {
    private final BrainstormId id;
    private final WorkspaceId workspaceId;
    private final List<Idea> ideas;
    private ConversionStatus status; // ACTIVE, CONVERTED_TO_PROJECT
    private ProjectId linkedProjectId;
}
```

- [ ] Create `BrainstormCreated` event
- [ ] Run test: should be GREEN

#### **Test: Add Ideas**

- [ ] Write test: `should_add_idea_to_brainstorm()`
- [ ] Create `Idea` entity
```java
public class Idea extends Entity {
    private final IdeaId id;
    private String title;
    private String description;
    private LocalDateTime createdAt;
    private boolean converted;
}
```

- [ ] Implement `addIdea()` method
- [ ] Create `IdeaAdded` event
- [ ] Run test: should be GREEN

#### **Test: Convert Idea**

- [ ] Write test: `should_mark_idea_as_converted()`
- [ ] Write test: `should_prevent_converting_already_converted_idea()`
- [ ] Implement `convertIdeaToProject()` method
```java
public IdeaData convertIdeaToProject(IdeaId ideaId) {
    var idea = findIdea(ideaId);

    if (idea.isConverted()) {
        throw new IdeaAlreadyConvertedException();
    }

    idea.markAsConverted();

    addEvent(new BrainstormIdeaConverted(
        this.id,
        this.workspaceId,
        idea.getId(),
        idea.getTitle(),
        idea.getDescription()
    ));

    return new IdeaData(idea.getTitle(), idea.getDescription());
}
```

- [ ] Run tests: should be GREEN

### Step 5.2: Implement Idea Conversion Saga

**Saga Flow:**
```
1. User → ConvertBrainstormToProjectCommand
2. Ideation Context: Mark idea as CONVERTED
   → Emit BrainstormIdeaConverted (with idea data)
3. Workspace Context (listens): Check subscription limits
   → Emit WorkspaceProjectCreationApproved (if allowed)
4. Project Planning Context (listens): Create project from idea data
   → Emit ProjectCreatedFromBrainstorm
5. Ideation Context (listens): Link brainstorm to created project
   → Emit BrainstormLinkedToProject
```

#### **Step 1: ConvertBrainstormToProjectCommand**

- [ ] Create `ConvertBrainstormToProjectCommand`
```java
public record ConvertBrainstormToProjectCommand(
    BrainstormId brainstormId,
    IdeaId ideaId,
    WorkspaceId workspaceId,
    UserId userId
) {}
```

- [ ] Create command handler
```java
public class ConvertBrainstormToProjectCommandHandler {
    private final BrainstormRepository brainstormRepository;
    private final EventBus eventBus;

    public void handle(ConvertBrainstormToProjectCommand command) {
        // Load brainstorm
        var brainstorm = brainstormRepository.findById(command.brainstormId());

        // Convert idea
        var ideaData = brainstorm.convertIdeaToProject(command.ideaId());

        // Save
        brainstormRepository.save(brainstorm);

        // Publish events
        publishEvents(brainstorm.getEvents());
    }
}
```

#### **Step 2: Project Creation from Brainstorm**

- [ ] Create `ProjectCreationFromBrainstormSaga`
```java
public class ProjectCreationFromBrainstormSaga implements EventHandler {
    private final WorkspaceRepository workspaceRepository;
    private final ProjectRepository projectRepository;
    private final WorkspaceProjectCountReadModel countReadModel;
    private final EventBus eventBus;

    @Override
    public void handle(DomainEvent event) {
        if (event instanceof BrainstormIdeaConverted e) {
            handleIdeaConversion(e);
        }
    }

    private void handleIdeaConversion(BrainstormIdeaConverted event) {
        // 1. Check workspace limits (same as CreateProject)
        var count = countReadModel.getCount(event.getWorkspaceId());
        var workspace = workspaceRepository.findById(event.getWorkspaceId());

        if (!workspace.approveProjectCreation(count)) {
            throw new ProjectLimitReachedException();
        }

        workspaceRepository.save(workspace);

        // 2. Create project with idea data
        var project = Project.createFromBrainstorm(
            event.getWorkspaceId(),
            event.getBrainstormId(),
            event.getIdeaTitle(),
            event.getIdeaDescription(),
            event.getUserId()
        );

        projectRepository.save(project);

        // 3. Publish events
        publishEvents(workspace.getEvents());
        publishEvents(project.getEvents());
    }
}
```

- [ ] Add `createFromBrainstorm()` factory method to Project
- [ ] Create `ProjectCreatedFromBrainstorm` event

#### **Step 3: Link Brainstorm to Project**

- [ ] Create `BrainstormLinkingSaga`
```java
public class BrainstormLinkingSaga implements EventHandler {
    private final BrainstormRepository brainstormRepository;

    @Override
    public void handle(DomainEvent event) {
        if (event instanceof ProjectCreatedFromBrainstorm e) {
            linkBrainstorm(e);
        }
    }

    private void linkBrainstorm(ProjectCreatedFromBrainstorm event) {
        var brainstorm = brainstormRepository.findById(event.getBrainstormId());
        brainstorm.linkToProject(event.getProjectId());
        brainstormRepository.save(brainstorm);
        publishEvents(brainstorm.getEvents());
    }
}
```

- [ ] Implement `linkToProject()` method on Brainstorm
- [ ] Create `BrainstormLinkedToProject` event

### Step 5.3: Wire Up Saga

- [ ] Register saga listeners
```java
eventBus.subscribe("BrainstormIdeaConverted", projectCreationFromBrainstormSaga);
eventBus.subscribe("ProjectCreatedFromBrainstorm", brainstormLinkingSaga);
```

### Step 5.4: Brainstorm Read Models

- [ ] Create `BrainstormListDTO`
- [ ] Create `BrainstormListReadModel`
- [ ] Create `BrainstormListProjection`
  - Listen to `BrainstormCreated`
  - Listen to `IdeaAdded` → increment idea count
  - Listen to `BrainstormLinkedToProject` → increment converted count

- [ ] Create `GetBrainstormListQuery` and handler

### Step 5.5: Integration Test

```java
@Test
void should_create_project_from_brainstorm_idea() {
    // Given: Brainstorm with an idea
    var brainstorm = createBrainstorm(workspaceId);
    brainstorm.addIdea("Video Idea", "Description");
    brainstormRepository.save(brainstorm);
    var ideaId = brainstorm.getIdeas().get(0).getId();

    // When: Convert idea to project
    var command = new ConvertBrainstormToProjectCommand(
        brainstorm.getId(),
        ideaId,
        workspaceId,
        userId
    );
    commandHandler.handle(command);

    // Then: Project should be created
    await().atMost(5, SECONDS).until(() -> {
        var projects = projectRepository.findByWorkspaceId(workspaceId);
        return projects.size() == 1 &&
               projects.get(0).getTitle().equals("Video Idea");
    });

    // And: Brainstorm should be linked
    var updatedBrainstorm = brainstormRepository.findById(brainstorm.getId());
    assertThat(updatedBrainstorm.getLinkedProjectId()).isNotNull();
}
```

- [ ] Write and run integration test

### Step 5.6: Phase 5 Completion Checklist

- [ ] Brainstorm aggregate implemented
- [ ] Ideas can be added and updated
- [ ] Idea-to-project conversion saga working end-to-end
- [ ] Project created from brainstorm data
- [ ] Brainstorm linked to created project
- [ ] Subscription limits still enforced during conversion
- [ ] Read models updated correctly
- [ ] Integration tests pass
- [ ] Test coverage maintained: 85%+
- [ ] Run full build: `mvn clean verify`
- [ ] Git commit: "Phase 5 complete: Ideation context with conversion saga"

---

## Phase 6: Production Infrastructure (Week 7-8)

### Goal
Replace in-memory implementations with production-ready infrastructure (PostgreSQL, EventStoreDB, real email service).

### Step 6.1: Setup PostgreSQL

- [ ] Install PostgreSQL locally or use Docker
```bash
docker run --name ccpp-postgres \
  -e POSTGRES_PASSWORD=password \
  -e POSTGRES_DB=ccpp \
  -p 5432:5432 \
  -d postgres:15
```

- [ ] Add PostgreSQL dependency to POMs
```xml
<dependency>
    <groupId>org.postgresql</groupId>
    <artifactId>postgresql</artifactId>
</dependency>
```

- [ ] Create schema migration scripts (using Flyway or Liquibase)

**V1__create_projections.sql:**
```sql
CREATE TABLE project_list_view (
    project_id VARCHAR(36) PRIMARY KEY,
    workspace_id VARCHAR(36) NOT NULL,
    title VARCHAR(255),
    status VARCHAR(20),
    total_budget DECIMAL(19,2),
    participant_count INT,
    created_at TIMESTAMP,
    INDEX idx_workspace (workspace_id)
);

CREATE TABLE project_detail_view (
    project_id VARCHAR(36) PRIMARY KEY,
    workspace_id VARCHAR(36) NOT NULL,
    title VARCHAR(255),
    description TEXT,
    status VARCHAR(20),
    budget_items JSONB,
    participants JSONB,
    notes JSONB,
    timeline JSONB
);

CREATE TABLE workspace_project_count_view (
    workspace_id VARCHAR(36) PRIMARY KEY,
    project_count INT,
    subscription_tier VARCHAR(20),
    max_projects INT
);
```

- [ ] Add Flyway dependency and configure
- [ ] Run migrations: `mvn flyway:migrate`

### Step 6.2: Implement JPA Read Model Repositories

- [ ] Create JPA entities for read models
```java
@Entity
@Table(name = "project_list_view")
public class ProjectListEntity {
    @Id
    private String projectId;
    private String workspaceId;
    private String title;
    private String status;
    private BigDecimal totalBudget;
    private int participantCount;
    private LocalDateTime createdAt;

    // Getters/Setters
}
```

- [ ] Create JPA repository interface
```java
public interface JpaProjectListRepository extends JpaRepository<ProjectListEntity, String> {
    List<ProjectListEntity> findByWorkspaceId(String workspaceId);
}
```

- [ ] Create adapter implementing `ProjectListReadModel`
```java
public class PostgresProjectListReadModel implements ProjectListReadModel {
    private final JpaProjectListRepository jpaRepository;

    @Override
    public void add(ProjectListDTO dto) {
        var entity = toEntity(dto);
        jpaRepository.save(entity);
    }

    // ... other methods
}
```

- [ ] Repeat for other read models
- [ ] Update configuration to use PostgreSQL read models instead of in-memory

### Step 6.3: Setup EventStoreDB

- [ ] Install EventStoreDB using Docker
```bash
docker run --name ccpp-eventstore \
  -d -p 2113:2113 -p 1113:1113 \
  eventstore/eventstore:latest \
  --insecure --run-projections=All \
  --enable-atom-pub-over-http
```

- [ ] Add EventStoreDB client dependency
```xml
<dependency>
    <groupId>com.eventstore</groupId>
    <artifactId>db-client-java</artifactId>
    <version>5.0.0</version>
</dependency>
```

- [ ] Create `EventStoreDbEventStore` implementation
```java
public class EventStoreDbEventStore implements EventStore {
    private final EventStoreDBClient client;
    private final ObjectMapper objectMapper;

    @Override
    public void append(String streamId, List<DomainEvent> events, int expectedVersion) {
        var eventDataList = events.stream()
            .map(this::toEventData)
            .collect(Collectors.toList());

        var options = AppendToStreamOptions.get()
            .expectedRevision(expectedVersion);

        client.appendToStream(streamId, options, eventDataList.iterator())
            .get();
    }

    @Override
    public List<DomainEvent> readStream(String streamId) {
        var readOptions = ReadStreamOptions.get()
            .forwards()
            .fromStart();

        var result = client.readStream(streamId, readOptions).get();

        return result.getEvents().stream()
            .map(this::toDomainEvent)
            .collect(Collectors.toList());
    }

    private EventData toEventData(DomainEvent event) {
        var json = objectMapper.writeValueAsBytes(event);
        return EventData.builderAsJson(
            event.getEventId().toString(),
            event.getEventType(),
            json
        ).build();
    }

    private DomainEvent toDomainEvent(ResolvedEvent resolved) {
        var eventType = resolved.getEvent().getEventType();
        var data = resolved.getEvent().getEventData();
        return objectMapper.readValue(data, getEventClass(eventType));
    }
}
```

- [ ] Implement event serialization/deserialization
- [ ] Update configuration to use EventStoreDB instead of in-memory

### Step 6.4: Implement Event Sourced Repository with EventStoreDB

- [ ] Update `EventSourcedProjectRepository` to use EventStoreDB
```java
public class EventSourcedProjectRepository implements ProjectRepository {
    private final EventStore eventStore;

    @Override
    public void save(Project project) {
        var events = project.getEvents();
        if (!events.isEmpty()) {
            var streamId = "project-" + project.getId();
            eventStore.append(streamId, events, project.getVersion());
            project.clearEvents();
        }
    }

    @Override
    public Optional<Project> findById(ProjectId projectId) {
        var streamId = "project-" + projectId;
        var events = eventStore.readStream(streamId);

        if (events.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(rebuildFromEvents(events));
    }

    private Project rebuildFromEvents(List<DomainEvent> events) {
        // Apply events to rebuild aggregate state
        Project project = null;
        for (var event : events) {
            if (event instanceof ProjectCreated e) {
                project = Project.fromEvents(e);
            } else {
                project.applyEvent(event);
            }
        }
        return project;
    }
}
```

- [ ] Implement event replay logic in Project aggregate
```java
public void applyEvent(DomainEvent event) {
    if (event instanceof BudgetItemAdded e) {
        applyBudgetItemAdded(e);
    } else if (event instanceof ProjectMarkedAsReady e) {
        applyProjectMarkedAsReady(e);
    }
    // ... handle all events
}
```

### Step 6.5: Setup Real Email Service

- [ ] Choose email provider (SendGrid, AWS SES, or Mailgun)
- [ ] Add dependency (example: SendGrid)
```xml
<dependency>
    <groupId>com.sendgrid</groupId>
    <artifactId>sendgrid-java</artifactId>
</dependency>
```

- [ ] Create `SendGridEmailService` implementation
```java
public class SendGridEmailService implements EmailService {
    private final SendGrid sendGridClient;

    @Override
    public void sendEmail(String to, String subject, String body) {
        Email from = new Email("noreply@ccpp.com");
        Email toEmail = new Email(to);
        Content content = new Content("text/html", body);
        Mail mail = new Mail(from, subject, toEmail, content);

        try {
            Request request = new Request();
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            sendGridClient.api(request);
        } catch (IOException ex) {
            throw new EmailSendingException("Failed to send email", ex);
        }
    }
}
```

- [ ] Add configuration for email templates
- [ ] Create email template for participant invitations

### Step 6.6: JWT Authentication in API Gateway

**Module:** `ApiGateway/`

- [ ] Add Spring Security dependencies
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt</artifactId>
</dependency>
```

- [ ] Create JWT authentication filter
```java
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) {
        var token = extractToken(request);

        if (token != null && validateToken(token)) {
            var claims = parseToken(token);
            var userId = claims.get("userId", String.class);
            var workspaceId = claims.get("workspaceId", String.class);

            // Set security context
            SecurityContext.setContext(
                new UserId(UUID.fromString(userId)),
                new WorkspaceId(UUID.fromString(workspaceId))
            );

            // Add to request headers for downstream services
            request.setAttribute("X-User-Id", userId);
            request.setAttribute("X-Workspace-Id", workspaceId);
        }

        filterChain.doFilter(request, response);
    }
}
```

- [ ] Configure Spring Security
```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf().disable()
            .authorizeRequests()
                .antMatchers("/api/auth/**").permitAll()
                .anyRequest().authenticated()
            .and()
            .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
```

### Step 6.7: REST Controllers

- [ ] Create REST controllers for each bounded context

**ProjectController:**
```java
@RestController
@RequestMapping("/api/projects")
public class ProjectController {
    private final CreateProjectCommandHandler createHandler;
    private final GetProjectListQueryHandler listHandler;

    @PostMapping
    public ResponseEntity<ProjectIdDTO> createProject(@RequestBody CreateProjectRequest request) {
        var context = SecurityContext.getContext();

        var command = new CreateProjectCommand(
            context.getWorkspaceId(),
            request.getTitle(),
            request.getDescription(),
            request.getTimeline(),
            context.getUserId()
        );

        var projectId = createHandler.handle(command);
        return ResponseEntity.ok(new ProjectIdDTO(projectId.toString()));
    }

    @GetMapping
    public ResponseEntity<List<ProjectListDTO>> listProjects() {
        var context = SecurityContext.getContext();
        var query = new GetProjectListQuery(context.getWorkspaceId());
        var projects = listHandler.handle(query);
        return ResponseEntity.ok(projects);
    }

    // ... other endpoints
}
```

- [ ] Create controllers for Workspace, Brainstorm, Notification contexts
- [ ] Add OpenAPI/Swagger documentation

### Step 6.8: Docker Compose Setup

- [ ] Create `docker-compose.yml`
```yaml
version: '3.8'

services:
  postgres:
    image: postgres:15
    environment:
      POSTGRES_DB: ccpp
      POSTGRES_USER: ccpp
      POSTGRES_PASSWORD: password
    ports:
      - "5432:5432"
    volumes:
      - postgres-data:/var/lib/postgresql/data

  eventstore:
    image: eventstore/eventstore:latest
    environment:
      - EVENTSTORE_CLUSTER_SIZE=1
      - EVENTSTORE_RUN_PROJECTIONS=All
      - EVENTSTORE_START_STANDARD_PROJECTIONS=true
      - EVENTSTORE_INSECURE=true
    ports:
      - "2113:2113"
      - "1113:1113"
    volumes:
      - eventstore-data:/var/lib/eventstore

  ccpp-api:
    build: .
    ports:
      - "8080:8080"
    depends_on:
      - postgres
      - eventstore
    environment:
      - SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/ccpp
      - EVENTSTORE_HOST=eventstore
      - EVENTSTORE_PORT=1113

volumes:
  postgres-data:
  eventstore-data:
```

- [ ] Create Dockerfile for application
```dockerfile
FROM maven:3.8-openjdk-17 AS build
WORKDIR /app
COPY pom.xml .
COPY */pom.xml ./
RUN mvn dependency:go-offline

COPY . .
RUN mvn clean package -DskipTests

FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=build /app/ApiGateway/target/ccpp-api.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

- [ ] Test Docker setup: `docker-compose up`

### Step 6.9: Phase 6 Completion Checklist

- [ ] PostgreSQL integrated for read models
- [ ] EventStoreDB integrated for event sourcing
- [ ] Event sourced repositories working with EventStoreDB
- [ ] Event replay working correctly
- [ ] Real email service configured (SendGrid/SES)
- [ ] JWT authentication in API Gateway
- [ ] REST controllers implemented for all contexts
- [ ] Security context populated from JWT
- [ ] Docker Compose setup working
- [ ] Application runs end-to-end with production infrastructure
- [ ] Integration tests updated for production setup
- [ ] Run full test suite: `mvn clean verify`
- [ ] Git commit: "Phase 6 complete: Production infrastructure"

---

## Phase 7: Testing + Documentation (Week 9-10)

### Goal
Comprehensive testing, performance validation, and complete documentation.

### Step 7.1: E2E Test Suite

- [ ] Setup E2E testing framework (REST Assured + Testcontainers)
```xml
<dependency>
    <groupId>io.rest-assured</groupId>
    <artifactId>rest-assured</artifactId>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>org.testcontainers</groupId>
    <artifactId>postgresql</artifactId>
    <scope>test</scope>
</dependency>
```

#### **E2E Test 1: Complete Project Lifecycle**

```java
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@Testcontainers
class ProjectLifecycleE2ETest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15");

    @Test
    void should_complete_full_project_lifecycle() {
        // 1. Create workspace
        var workspaceId = given()
            .contentType("application/json")
            .body(new CreateWorkspaceRequest("Team Joyca"))
        .when()
            .post("/api/workspaces")
        .then()
            .statusCode(201)
            .extract().body().as(WorkspaceIdDTO.class).id();

        // 2. Create project
        var projectId = given()
            .contentType("application/json")
            .header("Authorization", "Bearer " + getToken(workspaceId))
            .body(new CreateProjectRequest("Q1 Video", "Description", timeline))
        .when()
            .post("/api/projects")
        .then()
            .statusCode(201)
            .extract().body().as(ProjectIdDTO.class).id();

        // 3. Add budget items
        given()
            .contentType("application/json")
            .header("Authorization", "Bearer " + getToken(workspaceId))
            .body(new AddBudgetItemRequest("Hotel", 300))
        .when()
            .post("/api/projects/{projectId}/budget-items", projectId)
        .then()
            .statusCode(201);

        // 4. Add participants
        given()
            .contentType("application/json")
            .header("Authorization", "Bearer " + getToken(workspaceId))
            .body(new AddParticipantRequest("alice@example.com", "Alice"))
        .when()
            .post("/api/projects/{projectId}/participants", projectId)
        .then()
            .statusCode(201);

        // 5. Mark as ready
        given()
            .header("Authorization", "Bearer " + getToken(workspaceId))
        .when()
            .post("/api/projects/{projectId}/mark-ready", projectId)
        .then()
            .statusCode(200);

        // 6. Verify project is READY
        given()
            .header("Authorization", "Bearer " + getToken(workspaceId))
        .when()
            .get("/api/projects/{projectId}", projectId)
        .then()
            .statusCode(200)
            .body("status", equalTo("READY"))
            .body("totalBudget", equalTo(300))
            .body("participants", hasSize(1));

        // 7. Verify notifications sent
        await().atMost(10, SECONDS).until(() ->
            notificationRepository.findAll().stream()
                .anyMatch(n -> n.getStatus() == NotificationStatus.SENT)
        );
    }
}
```

- [ ] Write E2E test for project lifecycle
- [ ] Run test: should be GREEN

#### **E2E Test 2: Multi-Tenant Isolation**

```java
@Test
void should_enforce_multi_tenant_isolation() {
    // Given: Two workspaces with projects
    var workspace1Token = createWorkspaceAndGetToken("Workspace 1");
    var workspace2Token = createWorkspaceAndGetToken("Workspace 2");

    var project1Id = createProject(workspace1Token, "Project 1");
    var project2Id = createProject(workspace2Token, "Project 2");

    // When: Workspace 1 tries to access their projects
    var workspace1Projects = given()
        .header("Authorization", "Bearer " + workspace1Token)
    .when()
        .get("/api/projects")
    .then()
        .statusCode(200)
        .extract().body().as(new TypeRef<List<ProjectListDTO>>() {});

    // Then: Should only see Project 1
    assertThat(workspace1Projects).hasSize(1);
    assertThat(workspace1Projects.get(0).title()).isEqualTo("Project 1");

    // When: Workspace 1 tries to access Workspace 2's project
    given()
        .header("Authorization", "Bearer " + workspace1Token)
    .when()
        .get("/api/projects/{projectId}", project2Id)
    .then()
        .statusCode(403); // Forbidden
}
```

- [ ] Write E2E test for tenant isolation
- [ ] Run test: should be GREEN

#### **E2E Test 3: Subscription Limits Saga**

- [ ] Write E2E test for freemium limit enforcement
- [ ] Write E2E test for premium unlimited projects

#### **E2E Test 4: Brainstorm Conversion Saga**

- [ ] Write E2E test for idea-to-project conversion flow

- [ ] Verify all E2E tests pass: `mvn verify -Pe2e`

### Step 7.2: Performance Testing

- [ ] Setup JMeter or Gatling for load testing

**Gatling simulation example:**
```scala
class CCPPSimulation extends Simulation {

  val httpProtocol = http
    .baseUrl("http://localhost:8080")
    .acceptHeader("application/json")

  val scn = scenario("Project Creation")
    .exec(http("Create Project")
      .post("/api/projects")
      .header("Authorization", "Bearer ${token}")
      .body(StringBody("""{"title":"Load Test Project"}"""))
      .check(status.is(201)))
    .pause(1)

  setUp(
    scn.inject(
      rampUsers(100) during (60 seconds)
    )
  ).protocols(httpProtocol)
}
```

- [ ] Create performance test scenarios:
  - Project creation (100 concurrent users)
  - Read queries (200 concurrent users)
  - Mixed workload

- [ ] Run performance tests
- [ ] Verify performance targets:
  - [ ] API response time < 200ms (p95)
  - [ ] Event store write latency < 50ms
  - [ ] System handles 100+ concurrent users
  - [ ] No data corruption under load

### Step 7.3: Test Coverage Analysis

- [ ] Add JaCoCo plugin to pom.xml
```xml
<plugin>
    <groupId>org.jacoco</groupId>
    <artifactId>jacoco-maven-plugin</artifactId>
    <executions>
        <execution>
            <goals>
                <goal>prepare-agent</goal>
            </goals>
        </execution>
        <execution>
            <id>report</id>
            <phase>test</phase>
            <goals>
                <goal>report</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```

- [ ] Generate coverage report: `mvn test jacoco:report`
- [ ] Verify coverage targets met:
  - [ ] Domain layer: 95%+
  - [ ] Application layer: 85%+
  - [ ] Infrastructure layer: 70%+
  - [ ] Overall: 80%+

- [ ] Address any coverage gaps

### Step 7.4: API Documentation (OpenAPI)

- [ ] Add Springdoc OpenAPI dependency
```xml
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-ui</artifactId>
    <version>1.7.0</version>
</dependency>
```

- [ ] Add OpenAPI annotations to controllers
```java
@Operation(summary = "Create a new project", description = "Creates a project within the authenticated user's workspace")
@ApiResponses(value = {
    @ApiResponse(responseCode = "201", description = "Project created successfully"),
    @ApiResponse(responseCode = "403", description = "Project limit reached for freemium workspace"),
    @ApiResponse(responseCode = "400", description = "Invalid request")
})
@PostMapping
public ResponseEntity<ProjectIdDTO> createProject(@RequestBody CreateProjectRequest request) {
    // ...
}
```

- [ ] Configure Swagger UI
- [ ] Generate API documentation
- [ ] Access at: `http://localhost:8080/swagger-ui.html`
- [ ] Export OpenAPI spec: `http://localhost:8080/v3/api-docs`

### Step 7.5: Architecture Documentation

- [ ] Create `docs/ARCHITECTURE.md`
  - System overview diagram
  - Bounded context map
  - Event flow diagrams
  - Clean architecture layers diagram

- [ ] Create `docs/ADR/` (Architecture Decision Records)
  - [ ] ADR-001: Why Event Sourcing
  - [ ] ADR-002: Why Saga Choreography
  - [ ] ADR-003: Multi-Tenancy Strategy
  - [ ] ADR-004: Budget Inside Project Aggregate
  - [ ] ADR-005: In-Memory First, Production Later

**ADR Template:**
```markdown
# ADR-001: Event Sourcing for Audit Trail

## Status
Accepted

## Context
Content creators need complete audit trails of all changes to projects.
We need to support temporal queries and event-driven sagas.

## Decision
Implement Event Sourcing as the persistence strategy for all aggregates.

## Consequences
- Positive: Complete audit trail, temporal queries, event-driven architecture
- Negative: Increased complexity, learning curve, eventual consistency

## Alternatives Considered
- Traditional CRUD with audit logs: Rejected due to limited queryability
```

- [ ] Create ADRs for all major decisions

### Step 7.6: Developer Documentation

- [ ] Create `docs/DEVELOPMENT.md`
  - Setup instructions
  - How to run locally
  - How to run tests
  - How to add a new bounded context
  - How to add a new command/query
  - Coding standards

- [ ] Create `docs/TESTING.md`
  - Testing strategy
  - How to write domain tests
  - How to write integration tests
  - How to write E2E tests

- [ ] Update README.md
  - Project overview
  - Quick start guide
  - Architecture highlights
  - Links to detailed docs

### Step 7.7: Demo Preparation

- [ ] Create demo script
- [ ] Prepare demo data
- [ ] Create demo video walkthrough
- [ ] Prepare presentation slides highlighting:
  - DDD bounded contexts
  - Event Sourcing implementation
  - CQRS pattern
  - Saga choreography
  - Clean Architecture
  - Test coverage

### Step 7.8: Phase 7 Completion Checklist

- [ ] E2E test suite complete (4+ scenarios)
- [ ] Performance tests passing
- [ ] Test coverage: 80%+ overall, 95%+ domain
- [ ] API documentation (OpenAPI) complete
- [ ] Architecture documentation complete
- [ ] ADRs written for all major decisions
- [ ] Developer documentation complete
- [ ] README updated
- [ ] Demo prepared
- [ ] Final build successful: `mvn clean verify`
- [ ] All tests passing
- [ ] Performance targets met
- [ ] Git commit: "Phase 7 complete: Testing and documentation"
- [ ] Git tag: `v1.0.0`

---

## Final Verification Checklist

### Functional Requirements
- [ ] Projects can be created, updated, and marked as READY
- [ ] Budget items can be added, updated, and removed
- [ ] Total budget calculated correctly
- [ ] Projects are immutable when READY
- [ ] Participants can be invited
- [ ] Email notifications sent when project marked READY
- [ ] Participants can accept/decline invitations
- [ ] Brainstorms can be created at workspace level
- [ ] Ideas can be converted to projects
- [ ] Freemium workspaces limited to 2 projects
- [ ] Premium workspaces have unlimited projects
- [ ] Multi-tenant data isolation enforced

### Non-Functional Requirements
- [ ] Test coverage: 80%+ overall, 95%+ domain
- [ ] Build time: < 5 minutes
- [ ] API response time: < 200ms (p95)
- [ ] Event store write latency: < 50ms
- [ ] System handles 100+ concurrent users
- [ ] Complete audit trail via Event Sourcing
- [ ] All events can be replayed
- [ ] Projections eventually consistent

### Architecture Quality
- [ ] Clear bounded contexts with minimal coupling
- [ ] Rich domain models (not anemic)
- [ ] Ubiquitous language used throughout code
- [ ] Invariants enforced in aggregates
- [ ] Zero domain dependencies on frameworks
- [ ] Infrastructure easily swappable
- [ ] Business logic testable without infrastructure
- [ ] Event-driven sagas working correctly

### Documentation
- [ ] PLAN.md complete and accurate
- [ ] IMPLEMENTATION_STEPS.md (this document) followed
- [ ] ARCHITECTURE.md with diagrams
- [ ] ADRs for major decisions
- [ ] API documentation (OpenAPI)
- [ ] Developer documentation
- [ ] README with quick start

---

## Congratulations!

You've successfully implemented a production-ready DDD/CQRS/Event Sourcing application following Clean Architecture and TDD principles!

**Key Achievements:**
- 4 bounded contexts with clear boundaries
- Event sourcing with complete audit trail
- CQRS with optimized read models
- 3 saga choreographies
- Multi-tenant isolation
- 95%+ domain test coverage
- Production-ready infrastructure

**Next Steps:**
- Deploy to cloud (AWS/Azure/GCP)
- Add monitoring and observability (Prometheus, Grafana)
- Implement event versioning strategy
- Add more features (reporting, analytics, etc.)
- Scale horizontally with event bus (RabbitMQ/Kafka)
