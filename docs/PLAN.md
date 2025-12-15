# CCPP - Content Creator Planning Platform

## Project Overview

A **multi-tenant** content creator planning application demonstrating **Clean Architecture**, **Domain-Driven Design (
DDD)**, **Event Sourcing**, **CQRS**, **Event-Driven Architecture**, **Saga Choreography**, and **Test-Driven
Development (TDD)**.

### Purpose

Enable content creation teams to collaboratively plan video projects, manage budgets, brainstorm ideas, and coordinate
with external participants (guests/collaborators) - with full audit trails and multi-tenant isolation.

---

## Domain Understanding

### What is CCPP?

CCPP helps content creator teams (e.g., YouTube creators like "Joyca's team") to:

1. **Plan video projects** with timelines and budgets
2. **Track budget items** (e.g., "Hotel 2x nights: $300") with automatic total calculation
3. **Brainstorm ideas** at workspace level before converting to projects
4. **Invite external participants** (guests like "Mcfly&Carlito") who can accept/decline
5. **Send notifications** (email/SMS) when project is ready
6. **Isolate data by tenant** (Joyca's team can't see other creators' projects)

### Core Domain Insights

- **Project = Video Planning**: Has timeline, status, budget, participants
- **Status Transitions**: `PLANNING → READY` (immutable when READY)
- **Timeline Changes**: Only allowed while in `PLANNING` status
- **Budget Items**: Compositional part of project (delete project = delete budget)
- **Participants**: External collaborators (not team members) who receive plan and respond
- **Brainstorm**: Workspace-level idea board, can be converted to projects
- **Multi-tenancy**: Tenant = content creator team (multiple users share all projects)

### Business Rules Discovered

1. **Project Immutability**: When status = `READY`, nothing can be changed
2. **Budget Composition**: Budget items exist only within a project
3. **Subscription Limits**: Freemium = 2 projects max, Premium = unlimited
4. **Participant Workflow**: Participant can accept/decline; if declined, need replacement
5. **Workspace Isolation**: Projects filtered by `workspaceId` (tenantId)

---

## Bounded Contexts

### 1. **Workspace Context** (Multi-tenancy & Subscriptions)

**Purpose**: Manage tenant isolation and subscription-based business rules

**Aggregate**: `Workspace`

- `WorkspaceId` (Identity) - **This is the tenantId**
- `WorkspaceName` (Value Object)
- `SubscriptionTier` (Enum: FREEMIUM, PREMIUM)
- `Members` (Collection of workspace members)
- `ProjectCount` (Read from projection)

**Domain Events**:

- `WorkspaceCreated`
- `WorkspaceSubscriptionUpgraded`
- `WorkspaceMemberAdded`
- `WorkspaceProjectCreationApproved` (triggers saga)
- `WorkspaceProjectLimitReached`

**Invariants**:

- Freemium workspaces: max 2 projects
- Premium workspaces: unlimited projects
- Must have at least one owner

**Why Separate Context?**

- Subscription logic is independent of project planning
- Different lifecycle (workspace outlives projects)
- Different change reasons (pricing model vs project features)

---

### 2. **Ideation Context** (Brainstorming)

**Purpose**: Workspace-level idea management before project creation

**Aggregate**: `Brainstorm`

- `BrainstormId` (Identity)
- `WorkspaceId` (Reference - tenant isolation)
- `Ideas` (Collection of idea entities)
- `ConversionStatus` (Enum: ACTIVE, CONVERTED_TO_PROJECT)

**Domain Events**:

- `BrainstormCreated`
- `IdeaAdded`
- `IdeaUpdated`
- `BrainstormIdeaConverted` (triggers saga)
- `BrainstormLinkedToProject`

**Invariants**:

- Ideas belong to workspace (not specific project)
- Once converted, idea is marked to prevent duplicate conversion

**Why Separate Context?**

- Ideas exist before projects
- Different lifecycle (exploratory vs execution)
- Can have ideas without projects

---

### 3. **Project Planning Context** ⭐ **CORE DOMAIN**

**Purpose**: Video project planning with budget tracking and participant coordination

**Aggregate**: `Project` (Aggregate Root)

**Root Properties**:

- `ProjectId` (Identity)
- `WorkspaceId` (Reference - tenant isolation)
- `ProjectDetails` (Value Object: title, description)
- `Timeline` (Value Object: start date, end date)
- `Status` (Enum: PLANNING, READY)
- `CreatedBy` (UserId)
- `CreatedAt` (Timestamp)

**Entities** (inside aggregate):

- `BudgetItem`
    - `BudgetItemId` (Identity)
    - `Description` (e.g., "Hotel 2x nights")
    - `Amount` (Money Value Object)
    - `Category` (optional)

- `Participant`
    - `ParticipantId` (Identity)
    - `Email` (Email Value Object)
    - `Name` (String)
    - `InvitationStatus` (Enum: INVITED, ACCEPTED, DECLINED)
    - `ResponseAt` (Timestamp)

**Value Objects** (inside aggregate):

- `Note`
    - `content` (String)
    - `createdAt` (Timestamp)
    - `createdBy` (UserId)

**Domain Events**:

- `ProjectCreated`
- `ProjectCreatedFromBrainstorm`
- `ProjectDetailsUpdated`
- `ProjectTimelineChanged`
- `ProjectMarkedAsReady` (triggers participant invitation saga)
- `BudgetItemAdded`
- `BudgetItemUpdated`
- `BudgetItemRemoved`
- `NoteAdded`
- `ParticipantInvited`
- `ParticipantAccepted`
- `ParticipantDeclined` (may trigger replacement workflow)

**Invariants**:

1. **Immutability**: Cannot modify project when status = `READY`
2. **Timeline Validation**: End date must be after start date
3. **Timeline Lock**: Cannot change timeline when status = `READY`
4. **Budget Consistency**: Total budget = sum of all budget items
5. **Participant Uniqueness**: Cannot invite same email twice to same project
6. **Status Transition**: Can only go `PLANNING → READY` (no rollback)

**Business Logic Examples**:

```java
class Project {
    public Money getTotalBudget() {
        return budgetItems.stream()
                .map(BudgetItem::getAmount)
                .reduce(Money.ZERO, Money::add);
    }

    public void markAsReady(UserId userId) {
        if (status == READY) {
            throw new ProjectAlreadyReadyException();
        }

        this.status = READY;
        this.readyAt = Instant.now();

        addEvent(new ProjectMarkedAsReady(
                this.id,
                this.workspaceId,
                this.participants,
                userId
        ));
    }

    public void changeTimeline(DateRange newTimeline) {
        if (status == READY) {
            throw new CannotModifyReadyProjectException();
        }

        if (!newTimeline.isValid()) {
            throw new InvalidTimelineException();
        }

        this.timeline = newTimeline;
        addEvent(new ProjectTimelineChanged(this.id, newTimeline));
    }
}
```

**Why This is Core Domain?**

- Most complex business rules
- Main value proposition ("Create project with budget tracking")
- Where money is involved (budget)
- Critical invariants (immutability, consistency)

---

### 4. **Notification Context** (Supporting)

**Purpose**: Cross-context communication via email/SMS

**Aggregate**: `Notification`

- `NotificationId` (Identity)
- `Recipients` (List of emails/phone numbers)
- `NotificationType` (Enum: EMAIL, SMS)
- `TemplateId` (Reference to template)
- `Payload` (JSON data for template)
- `SentAt` (Timestamp)
- `Status` (Enum: PENDING, SENT, FAILED)

**Domain Events**:

- `NotificationScheduled`
- `NotificationSent`
- `NotificationFailed`

**Why Separate Context?**

- Generic infrastructure concern
- No project-specific business rules
- Reacts to events from other contexts
- Could be replaced by external service (SendGrid, Twilio)

---

## Saga Choreography (Event-Driven)

### Saga 1: **Create Project with Subscription Check**

```

 Choreography Flow                                    
$
 1. User → CreateProjectCommand                      
    ↓                                                 
 2. Workspace Context                                
    → Check subscription tier                         
    → Check current project count (from read model)  
    → If FREEMIUM && count >= 2: REJECT              
    → If allowed: Emit WorkspaceProjectCreationApproved
    ↓                                                 
 3. Project Planning Context (listens)               
    → Create Project aggregate                        
    → Emit ProjectCreated                             
    ↓                                                 
 4. Workspace Read Model (listens)                   
    → Increment project count for workspace           
```

**Why Saga?**

- Cross-context coordination (Workspace → Project)
- Business rule in one context affects creation in another
- Eventually consistent (read model updates async)

---

### Saga 2: **Participant Invitation Flow**

```

 Choreography Flow                                    
$
 1. Project marked as READY                          
    → ProjectMarkedAsReady event                      
    ↓                                                 
 2. Notification Context (listens)                   
    → For each participant in event.participants     
    → Send email with project details                 
    → Emit NotificationSent                           
    ↓                                                 
 3. Participant receives email                       
    → Clicks Accept/Decline link                      
    → API receives ParticipantResponseCommand         
    ↓                                                 
 4. Project Planning Context                         
    → Update participant status                       
    → If ACCEPTED: Emit ParticipantAccepted           
    → If DECLINED: Emit ParticipantDeclined           
    ↓                                                 
 5. (Optional) Auto-suggest replacement              
    → If ParticipantDeclined → trigger workflow       
```

**Why Choreography?**

- No central orchestrator
- Each context reacts independently
- Loose coupling
- Easy to add new listeners (e.g., analytics)

---

### Saga 3: **Brainstorm to Project Conversion**

```

 Choreography Flow                                    
 1. User → ConvertBrainstormToProjectCommand          
    ↓                                                 
 2. Ideation Context                                 
    → Mark idea as CONVERTED                          
    → Emit BrainstormIdeaConverted                    
       (with idea data: title, description)           
    ↓                                                 
 3. Workspace Context (listens)                      
    → Check subscription limits                       
    → Emit WorkspaceProjectCreationApproved           
    ↓                                                 
 4. Project Planning Context (listens)               
    → Create project from brainstorm data             
    → Emit ProjectCreatedFromBrainstorm               
    ↓                                                 
 5. Ideation Context (listens)                       
    → Link brainstorm to created project              
    → Emit BrainstormLinkedToProject                  
```

**Why Saga?**

- Cross-context operation (Ideation → Workspace → Project)
- Data transformation (idea → project)
- Multiple steps can fail independently

---

## Event Sourcing Strategy

### Event Store Design

**Event Stream per Aggregate**:

```
project-{projectId}        ↓ All project events
workspace-{workspaceId}    ↓ All workspace events
brainstorm-{brainstormId}  ↓ All brainstorm events
notification-{notificationId} ↓ All notification events
```

**Event Structure**:

```json
{
  "eventId": "uuid",
  "eventType": "ProjectCreated",
  "aggregateId": "project-123",
  "workspaceId": "workspace-456",  // CRITICAL for multi-tenant filtering
  "version": 1,                     // Optimistic concurrency control
  "timestamp": "2025-12-09T10:30:00Z",
  "data": { /* event payload */ },
  "metadata": {
    "userId": "user-789",
    "correlationId": "corr-abc",    // Track saga flows
    "causationId": "event-xyz"      // Event causation chain
  }
}
```

### Projections (Read Models)

**Why CQRS?**

- **Write side**: Event-sourced aggregates (complex, consistent)
- **Read side**: Denormalized views (fast, optimized for queries)

**Read Models**:

1. **ProjectListView** (for dashboard)

```sql
CREATE TABLE project_list_view
(
    project_id        VARCHAR PRIMARY KEY,
    workspace_id      VARCHAR NOT NULL, -- Multi-tenant filter
    title             VARCHAR,
    status            VARCHAR,
    total_budget      DECIMAL,
    participant_count INT,
    created_at        TIMESTAMP,
    INDEX             idx_workspace(workspace_id)
);
```

2. **ProjectDetailView** (for project page)

```sql
CREATE TABLE project_detail_view
(
    project_id   VARCHAR PRIMARY KEY,
    workspace_id VARCHAR NOT NULL,
    title        VARCHAR,
    description  TEXT,
    status       VARCHAR,
    budget_items JSONB, -- Denormalized
    participants JSONB, -- Denormalized
    notes        JSONB, -- Denormalized
    timeline     JSONB
);
```

3. **WorkspaceProjectCountView** (for subscription limits)

```sql
CREATE TABLE workspace_project_count_view
(
    workspace_id      VARCHAR PRIMARY KEY,
    project_count     INT,
    subscription_tier VARCHAR,
    max_projects      INT
);
```

4. **BrainstormListView**

```sql
CREATE TABLE brainstorm_list_view
(
    brainstorm_id   VARCHAR PRIMARY KEY,
    workspace_id    VARCHAR NOT NULL,
    idea_count      INT,
    converted_count INT,
    updated_at      TIMESTAMP
);
```

### Event Sourcing Benefits

1. **Complete Audit Trail**: Every change to every project is recorded
2. **Time Travel**: Rebuild project state at any point in history
3. **Debugging**: Replay events to understand "how did we get here?"
4. **Analytics**: Rich data for insights (e.g., "average time from PLANNING to READY")
5. **Event-Driven**: Natural integration with sagas and notifications
6. **Compliance**: Immutable history for legal/audit purposes

---

## Clean Architecture Layers

### Layer Structure

```

 Presentation Layer (API Gateway)                        
 - REST Controllers                                      
 - Authentication (JWT)                                  
 - Extract userId + workspaceId from token              
 - Route to bounded contexts                             
              ↓ depends on ↓

 Application Layer                                       
 - Command Handlers (write side)                         
 - Query Handlers (read side)                            
 - DTOs (Data Transfer Objects)                          
 - Event Handlers (saga listeners)                       
 - Application Services                                  
              ↓ depends on ↓

 Domain Layer (CORE - NO DEPENDENCIES)                   
 - Aggregates (Project, Workspace, Brainstorm)          
 - Entities (BudgetItem, Participant)                    
 - Value Objects (Money, DateRange, Email)              
 - Domain Events                                          
 - Repository Interfaces (ports)                         
 - Invariants & Business Rules                           
              ↓ implemented by ↓
              
 Infrastructure Layer                                    
 - Event Store Implementation (in-memory → EventStoreDB) 
 - Repository Implementations                            
 - Projection Updaters                                   
 - Email/SMS Services                                    
 - Database Adapters                                     
```

### Dependency Rules

```
Domain Layer:
  - ZERO external dependencies
  - Pure Java (no frameworks)
  - Contains all business logic

Application Layer:
  - Depends ONLY on Domain
  - Orchestrates use cases
  - Framework-agnostic

Infrastructure Layer:
  - Depends on Domain & Application
  - Implements interfaces defined in Domain
  - Contains framework code (Spring, etc.)

Presentation Layer:
  - Depends on Application
  - HTTP/REST concerns
  - Authentication/Authorization
```

### CQRS Pattern

**Commands (Write Side)**:

```
User Request: POST /projects
  ↓
CreateProjectCommand {
    workspaceId,
    title,
    description,
    timeline
}
  ↓
CreateProjectCommandHandler
  ↓
Workspace.approveProjectCreation() (check limits)
  ↓
WorkspaceProjectCreationApproved event
  ↓
Project.create() (aggregate method)
  ↓
ProjectCreated event
  ↓
EventStore.append(events)
  ↓
Event published to event bus
  ↓
ProjectListProjection.handle(ProjectCreated)
  ↓
Return: ProjectId
```

**Queries (Read Side)**:

```
User Request: GET /projects?workspaceId=123
  ↓
GetProjectsQuery { workspaceId }
  ↓
GetProjectsQueryHandler
  ↓
ProjectListReadRepository.findByWorkspaceId(workspaceId)
  ↓
Return: List<ProjectListDTO>
```

---

## Test-Driven Development (TDD)

### Testing Strategy

**Test Pyramid**:

```
                 /\
                /  \
               / E2E \          (10% - Full workflows)
              /--------\
             /          \
            / Integration \     (30% - Command/Query handlers)
           /--------------\
          /                \
         /   Unit Tests     \   (60% - Domain logic)
        /--------------------\
```

### Domain Layer Tests (Unit Tests)

**Test File**: `ProjectTest.java`

```java
class ProjectTest {

    @Test
    void should_create_project_and_emit_event() {
        // Given
        var workspaceId = new WorkspaceId(UUID.randomUUID());
        var timeline = new DateRange(LocalDate.of(2025, 1, 1), LocalDate.of(2025, 3, 31));

        // When
        var project = Project.create(workspaceId, "Q1 Video Series", "Educational content", timeline, userId);

        // Then
        assertThat(project.getStatus()).isEqualTo(Status.PLANNING);
        assertThat(project.getEvents()).hasSize(1).first().isInstanceOf(ProjectCreated.class);
    }

    @Test
    void should_prevent_modifications_when_ready() {
        // Given
        var project = createPlanningProject();
        project.markAsReady(userId);

        // When / Then
        assertThatThrownBy(() -> project.changeTimeline(newTimeline))
                .isInstanceOf(CannotModifyReadyProjectException.class);
    }

    @Test
    void should_calculate_total_budget() {
        // Given
        var project = createPlanningProject();
        project.addBudgetItem("Hotel", Money.of(300, "USD"));
        project.addBudgetItem("Equipment", Money.of(150, "USD"));

        // Then
        assertThat(project.getTotalBudget()).isEqualTo(Money.of(450, "USD"));
    }
}
```

**Test Coverage Goals**:

- **Domain Layer**: 95%+ (critical business logic)
- **Application Layer**: 85%+ (use case flows)
- **Infrastructure Layer**: 70%+ (integration points)
- **Overall**: 80%+

### TDD Workflow

```
RED ↓ GREEN ↓ REFACTOR

1. Write failing test (Red)
   ↓ Test describes desired behavior

2. Write minimal code to pass (Green)
   ↓ Just enough to make test pass

3. Refactor (Clean)
   ↓ Improve code quality while keeping tests green

4. Repeat
   ↓ Next invariant, next test
```

---

## Multi-Tenancy Strategy

### Tenant Isolation

**WorkspaceId (TenantId) everywhere**:

```java
// Domain Layer
class Project {
    private final WorkspaceId workspaceId; // Immutable after creation
}

// Application Layer
class CreateProjectCommand {
    private final WorkspaceId workspaceId; // From authenticated context
}

// Infrastructure Layer
@Query("SELECT p FROM Project p WHERE p.workspaceId = :workspaceId")
List<Project> findByWorkspaceId(WorkspaceId workspaceId);
```

### API Gateway Authentication Flow

```
1. Client sends request with JWT
   Authorization: Bearer <token>

2. API Gateway validates JWT
   ↓ Extract claims: { userId: "123", workspaceId: "456" }

3. API Gateway adds to request context
   X-User-Id: 123
   X-Workspace-Id: 456

4. Routes to bounded context service
   ↓ Service extracts from headers

5. Application layer creates command
   new CreateProjectCommand(
       workspaceId,  // From context
       userId,       // From context
       requestData
   )

6. Domain enforces tenant isolation
   ↓ All queries filtered by workspaceId
```

### Database-Level Isolation

**Option 1: Row-Level Security** (Single database)

```sql
-- Every query automatically filtered
CREATE POLICY tenant_isolation ON projects
    USING (workspace_id = current_setting('app.current_workspace')::uuid);
```

**Option 2: Schema per Tenant** (Better isolation)

```
Database: ccpp
 workspace_456/  (schema)
    projects
    event_store
    projections
 workspace_789/  (schema)
     projects
     event_store
     projections
```

**Start Simple**: Use Option 1 (row-level filter) initially, can migrate to Option 2 later.

---

## Technology Stack

### Backend (Java + Spring Boot)

```xml

<dependencies>
    <!-- Spring Boot -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>

    <!-- Event Store (Start with in-memory) -->
    <!-- Later: EventStoreDB client -->

    <!-- Database (for projections) -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-jpa</artifactId>
    </dependency>
    <dependency>
        <groupId>com.h2database</groupId>
        <artifactId>h2</artifactId> <!-- In-memory, later PostgreSQL -->
    </dependency>

    <!-- Testing -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-test</artifactId>
    </dependency>
    <dependency>
        <groupId>org.assertj</groupId>
        <artifactId>assertj-core</artifactId>
    </dependency>
</dependencies>
```

### Modules (Maven Multi-Module)

```
ccpp/
 pom.xml (parent)
 shared/ (shared kernel - value objects, base classes)
 ApiGateway/ (authentication, routing)
 ProjectPlanning/ (core domain - project planning context)
 Workspace/ (multi-tenancy & subscriptions - workspace context)
 Ideation/ (brainstorming - ideation context)
 Notification/ (supporting domain - notification context)
```

**Note**: Module names now match bounded context names for clarity and alignment with ubiquitous language.

### Infrastructure Adapters

**Phase 1: In-Memory** (for rapid TDD)

- `InMemoryEventStore`
- `InMemoryProjectRepository`
- `InMemoryEmailService` (logs to console)

**Phase 2: Production**

- `EventStoreDbEventStore` (EventStoreDB)
- `JpaProjectRepository` (PostgreSQL)
- `SendGridEmailService` (real emails)

---

## Implementation Roadmap

### Phase 1: Foundation + Project Core (Week 1-2)

**Goals**:

- ✅ Project structure with Clean Architecture
- ✅ Domain model for Project aggregate
- ✅ Event sourcing infrastructure (in-memory)
- ✅ TDD test suite for domain

**Tasks**:

1. Setup Maven multi-module project
2. Create `shared` module with base classes
3. Implement `Project` aggregate with TDD
    - Create project
    - Add budget items (calculate total)
    - Add notes
    - Mark as READY (immutability)
    - Timeline changes (only in PLANNING)
4. Implement in-memory event store
5. Write 20+ domain tests

**Success Criteria**:

- All domain tests passing
- 95%+ domain test coverage
- Event store stores and retrieves events

---

### Phase 2: CQRS + Projections (Week 3)

**Goals**:

- ✅ Command handlers (write side)
- ✅ Query handlers (read side)
- ✅ Projections from events

**Tasks**:

1. Implement command handlers
    - `CreateProjectCommandHandler`
    - `AddBudgetItemCommandHandler`
    - `MarkProjectAsReadyCommandHandler`
2. Implement query handlers
    - `GetProjectDetailsQueryHandler`
    - `GetProjectListQueryHandler`
3. Create projection updaters
    - `ProjectListProjection` (listens to events)
    - `ProjectDetailProjection`
4. In-memory projection store

**Success Criteria**:

- Commands create events
- Projections update from events
- Queries return denormalized data

---

### Phase 2.5: REST API + Basic Authentication (Week 3.5)

**Goals**:

- ✅ REST Controllers for Project Planning
- ✅ Basic HTTP authentication (no JWT yet)
- ✅ E2E tests via HTTP
- ✅ Validate CQRS architecture end-to-end

**Tasks**:

1. **REST Controllers** in `project-planning` module
   - `ProjectCommandController` (POST /api/projects, POST /api/projects/{id}/budget-items, etc.)
   - `ProjectQueryController` (GET /api/projects, GET /api/projects/{id})
   - Request/Response DTOs (separate from domain/query DTOs)
   - Exception handling (@ControllerAdvice)

2. **Simple Authentication Middleware**
   - Extract `X-Workspace-Id` and `X-User-Id` from headers
   - No JWT validation yet (hardcoded for testing)
   - Multi-tenant enforcement at controller level

3. **API Gateway Configuration** (basic routing)
   - Route `/api/projects/**` to project-planning service
   - Pass through authentication headers

4. **E2E HTTP Tests**
   - `@SpringBootTest(webEnvironment = RANDOM_PORT)`
   - TestRestTemplate for HTTP calls
   - Full project lifecycle via HTTP (create → add budget → mark ready)
   - Test multi-tenant isolation via headers

5. **OpenAPI Documentation** (basic)
   - Swagger/Springdoc integration
   - Document all endpoints
   - Available at `/swagger-ui.html`

**API Endpoints to Implement**:

```
POST   /api/projects                              → CreateProjectCommand
GET    /api/projects?workspaceId={id}            → GetProjectListQuery
GET    /api/projects/{id}                        → GetProjectDetailQuery
PUT    /api/projects/{id}                        → UpdateProjectDetailsCommand
PUT    /api/projects/{id}/timeline               → ChangeProjectTimelineCommand
POST   /api/projects/{id}/budget-items           → AddBudgetItemCommand
PUT    /api/projects/{id}/budget-items/{itemId}  → UpdateBudgetItemCommand
DELETE /api/projects/{id}/budget-items/{itemId}  → RemoveBudgetItemCommand
POST   /api/projects/{id}/participants           → InviteParticipantCommand
POST   /api/projects/{id}/notes                  → AddNoteCommand
POST   /api/projects/{id}/ready                  → MarkProjectAsReadyCommand
```

**Request/Response DTO Examples**:

```java
// Request DTO (API layer)
public record CreateProjectRequest(
    String title,
    String description,
    LocalDate startDate,
    LocalDate endDate,
    BigDecimal budgetLimit
) {}

// Response DTO (API layer)
public record ProjectResponse(
    String projectId,
    String workspaceId,
    String title,
    String description,
    String status,
    List<BudgetItemResponse> budgetItems,
    DateRangeResponse timeline
) {
    static ProjectResponse from(ProjectDetailDTO dto) { ... }
}
```

**Success Criteria**:

- ✅ All command/query handlers accessible via HTTP
- ✅ E2E tests pass (HTTP → Controller → Handler → EventStore → Projection → Query)
- ✅ Multi-tenant isolation enforced (workspaceId in headers)
- ✅ Swagger UI shows all endpoints
- ✅ Can demo working API with Postman/curl
- ✅ No JWT yet (deferred to Phase 6)

**Why Phase 2.5 Now?**

1. Validates CQRS architecture works end-to-end
2. Enables immediate testing/demo via HTTP
3. Provides feedback before building more bounded contexts
4. More motivating to see working API
5. API Gateway routing can be simple (defer complex auth to Phase 6)

---

### Phase 3: Workspace + Multi-Tenancy (Week 4)

**Goals**:

- ✅ Workspace bounded context
- ✅ Subscription limits
- ✅ Tenant isolation

**Tasks**:

1. Implement `Workspace` aggregate
2. Subscription tier logic (freemium/premium)
3. Project creation approval saga
4. Add `workspaceId` to all aggregates (already done)
5. Filter all queries by `workspaceId` (already done)
6. API Gateway context injection (basic version done in Phase 2.5, enhance here)

**Success Criteria**:

- Freemium limited to 2 projects
- Premium unlimited projects
- Cross-tenant data invisible

---

### Phase 4: Participant Saga (Week 5)

**Goals**:

- ✅ Participant entities
- ✅ Invitation workflow saga
- ✅ Email notifications

**Tasks**:

1. Add `Participant` entity to Project
2. `ParticipantInvited` event
3. Notification context (in-memory email service)
4. Saga choreography
    - ProjectMarkedAsReady ↓ send invitations
    - Participant response ↓ update project
5. Participant acceptance/decline UI

**Success Criteria**:

- Emails sent when project marked READY
- Participants can accept/decline
- Project shows participant status

---

### Phase 5: Ideation + Brainstorm (Week 6)

**Goals**:

- ✅ Brainstorm bounded context
- ✅ Idea to project conversion saga

**Tasks**:

1. Implement `Brainstorm` aggregate
2. Workspace-level brainstorm UI
3. Conversion saga
    - BrainstormIdeaConverted ↓ create project
4. Link brainstorm to created project

**Success Criteria**:

- Ideas can be added at workspace level
- Conversion creates project with brainstorm data
- Brainstorm marked as converted

---

### Phase 6: Production Infrastructure (Week 7-8)

**Goals**:

- ✅ Replace in-memory adapters
- ✅ Real event store
- ✅ Real database
- ✅ Real email service

**Tasks**:

1. Setup PostgreSQL
2. Integrate EventStoreDB
3. Implement JPA repositories
4. SendGrid/Twilio integration
5. JWT authentication in API Gateway
6. Docker Compose setup

**Success Criteria**:

- Events persisted in EventStoreDB
- Projections in PostgreSQL
- Emails sent via SendGrid
- Multi-tenant data isolated

---

### Phase 7: Testing + Documentation (Week 9-10)

**Goals**:

- ✅ E2E tests
- ✅ Performance testing
- ✅ Documentation

**Tasks**:

1. E2E test suite
    - Full project lifecycle
    - Saga flows
    - Multi-tenant isolation
2. Load testing (100 concurrent users)
3. API documentation (OpenAPI)
4. Architecture decision records (ADRs)
5. Demo preparation

**Success Criteria**:

- 80%+ overall test coverage
- API responds < 200ms (p95)
- Complete documentation

---

## Key Architectural Decisions

### Why Event Sourcing?

**Pros**:

- Complete audit trail (content creators need to track changes)
- Temporal queries (see project state at any point)
- Natural event-driven architecture (sagas)
- Debugging (replay events)

**Cons**:

- Complexity (event versioning)
- Learning curve
- Eventual consistency

**Decision**: Worth it for learning exercise + audit requirements

---

### Why Saga Choreography (not Orchestration)?

**Choreography** (chosen):

- Decentralized (no single point of failure)
- Loose coupling between contexts
- Easy to add new listeners
- Scales better

**Orchestration** (not chosen):

- Central orchestrator (single responsibility)
- Easier to visualize flow
- But: tight coupling, single point of failure

**Decision**: Choreography fits event-driven architecture better

---

### Why Multi-Tenancy at Domain Level?

**Alternative**: Handle at infrastructure (database level only)

**Chosen**: `workspaceId` in domain model

**Why**:

- Explicit business rule (projects belong to workspaces)
- Type safety (can't forget to filter)
- Domain events include workspace context
- Clearer in code

---

### Why Budget inside Project (not separate aggregate)?

**Reasons**:

- Strong lifecycle coupling (delete project = delete budget)
- Invariant: total budget affects project decisions
- No independent business rules for budget
- Consistency boundary is clear

**Alternative considered**: Separate Workplace context
**Decision**: Keep inside Project for simplicity and consistency

---

## Success Metrics

### Technical Excellence

- ✅ Test Coverage: 80%+ overall, 95%+ domain
- ✅ Build Time: < 5 minutes
- ✅ API Response: < 200ms (p95)
- ✅ Event Store: < 50ms write latency

### DDD Quality

- ✅ Clear bounded contexts with minimal coupling
- ✅ Rich domain models (not anemic)
- ✅ Ubiquitous language in code
- ✅ Invariants enforced in aggregates

### Event Sourcing Quality

- ✅ All state changes as events
- ✅ Projections eventually consistent
- ✅ Event replay works correctly
- ✅ Correlation IDs track sagas

### Clean Architecture Quality

- ✅ Zero domain dependencies on frameworks
- ✅ Infrastructure easily swappable
- ✅ Business logic testable without infrastructure

---

## Next Steps

1. ✅  **Review this plan** - Ensure alignment with learning goals
2. ⏭️ **Setup project structure** - Maven multi-module with clean architecture
3. ⏭️ **Start TDD** - First test: `should_create_project_with_valid_data()`
4. ⏭️ **Implement Project aggregate** - Core domain first
5. ⏭️ **Build iteratively** - One feature at a time, always tested

---

## Resources

### Books

- "Domain-Driven Design" - Eric Evans
- "Implementing Domain-Driven Design" - Vaughn Vernon
- "Clean Architecture" - Robert C. Martin
- "Test-Driven Development: By Example" - Kent Beck

### Event Sourcing

- Greg Young's talks on Event Sourcing
- EventStoreDB documentation
- Martin Fowler's "Event Sourcing" article

### Patterns

- Enterprise Integration Patterns (saga patterns)
- CQRS Journey (Microsoft)

---
