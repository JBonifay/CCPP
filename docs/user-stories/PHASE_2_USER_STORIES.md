# Phase 2: CQRS + Projections - User Stories

## Overview
Phase 2 implements the CQRS (Command Query Responsibility Segregation) pattern, separating write operations (commands) from read operations (queries) with optimized projections.

---

## Epic: Command Side (Write Model)

### User Story 2.1: Execute Create Project Command

**As a** content creator
**I want to** create a project through a command handler
**So that** my intent is processed through the application layer

#### Acceptance Criteria

- [ ] **Given** I am authenticated
  - **When** I send a CreateProjectCommand with workspace, title, description, timeline
  - **Then** the command handler processes the command
  - **And** calls the domain aggregate's factory method
  - **And** saves the project via repository
  - **And** publishes domain events to event bus
  - **And** returns the new ProjectId

- [ ] **Given** I send a CreateProjectCommand with invalid data
  - **When** the command handler processes it
  - **Then** validation fails before reaching domain
  - **And** returns appropriate error

#### Technical Notes
- Command is a simple DTO/record
- Command handler orchestrates use case
- No business logic in command handler (delegate to domain)

---

### User Story 2.2: Execute Add Budget Item Command

**As a** content creator
**I want to** add budget items through commands
**So that** my operations are processed consistently

#### Acceptance Criteria

- [ ] **Given** I have a project
  - **When** I send an AddBudgetItemCommand
  - **Then** the handler loads the project
  - **And** calls project.addBudgetItem()
  - **And** saves the project
  - **And** publishes BudgetItemAdded event
  - **And** returns the BudgetItemId

- [ ] **Given** I send a command for non-existent project
  - **When** the handler processes it
  - **Then** returns "ProjectNotFoundException"

---

### User Story 2.3: Execute Multiple Commands

**As a** developer
**I want** command handlers for all domain operations
**So that** all write operations go through application layer

#### Acceptance Criteria

- [ ] **Given** all domain operations
  - **When** I implement command handlers
  - **Then** UpdateBudgetItemCommandHandler is implemented
  - **And** RemoveBudgetItemCommandHandler is implemented
  - **And** MarkProjectAsReadyCommandHandler is implemented
  - **And** ChangeProjectTimelineCommandHandler is implemented
  - **And** AddNoteCommandHandler is implemented

- [ ] **Given** any command handler
  - **When** an exception occurs in domain logic
  - **Then** the handler properly propagates the exception
  - **And** no events are published

---

## Epic: Event Bus (Event-Driven Architecture)

### User Story 2.4: Publish Domain Events

**As a** developer
**I want** domain events published to an event bus
**So that** other components can react to state changes

#### Acceptance Criteria

- [ ] **Given** a command handler saves an aggregate
  - **When** the aggregate has uncommitted events
  - **Then** all events are published to the event bus
  - **And** events are published in order
  - **And** events are cleared from aggregate after publishing

- [ ] **Given** multiple handlers are subscribed to an event
  - **When** the event is published
  - **Then** all handlers receive the event
  - **And** handlers execute (async is acceptable for Phase 2)

#### Technical Notes
- InMemoryEventBus for Phase 2
- Simple pub/sub pattern
- Event type used as routing key

---

### User Story 2.5: Subscribe to Domain Events

**As a** developer
**I want to** subscribe handlers to specific event types
**So that** projections and sagas can react to events

#### Acceptance Criteria

- [ ] **Given** I have an event handler
  - **When** I subscribe it to "ProjectCreated" events
  - **Then** the handler is registered with the event bus
  - **And** when ProjectCreated is published, my handler is called

- [ ] **Given** I subscribe multiple handlers to same event type
  - **When** the event is published
  - **Then** all handlers are called
  - **And** execution order is not guaranteed (acceptable for Phase 2)

#### Technical Notes
- EventHandler interface with handle(DomainEvent) method
- Handlers stored in map: eventType → List<EventHandler>

---

## Epic: Query Side (Read Model)

### User Story 2.6: Project List View (Read Model)

**As a** content creator
**I want to** see a list of all my projects
**So that** I can quickly browse and access them

#### Acceptance Criteria

- [ ] **Given** I have created several projects
  - **When** I query for my workspace's project list
  - **Then** I receive a list of ProjectListDTO objects
  - **And** each DTO contains: projectId, workspaceId, title, status, totalBudget, participantCount, createdAt
  - **And** only projects from my workspace are returned

- [ ] **Given** I have no projects
  - **When** I query for project list
  - **Then** I receive an empty list (not an error)

#### Technical Notes
- Read model is separate from write model (CQRS)
- ProjectListDTO is optimized for list view
- No aggregate loading for queries

---

### User Story 2.7: Project Detail View (Read Model)

**As a** content creator
**I want to** view full details of a project
**So that** I can see all information including budget items, notes, and participants

#### Acceptance Criteria

- [ ] **Given** I have a project with budget items and notes
  - **When** I query for project details by ID
  - **Then** I receive a ProjectDetailDTO
  - **And** the DTO includes: projectId, title, description, status, timeline
  - **And** the DTO includes embedded budget items list
  - **And** the DTO includes embedded notes list
  - **And** the DTO includes total budget

- [ ] **Given** I query for a non-existent project
  - **When** the handler processes the query
  - **Then** returns empty Optional or throws NotFoundException

- [ ] **Given** I query for a project from another workspace
  - **When** the handler processes the query
  - **Then** access is denied (tested in Phase 3)

#### Technical Notes
- Denormalized view for fast access
- Budget items stored as JSONB or embedded in DTO
- No joins needed

---

## Epic: Projections (Event Handlers for Read Models)

### User Story 2.8: Update Project List Projection

**As a** system
**I want** the project list read model to stay synchronized with events
**So that** queries return up-to-date data

#### Acceptance Criteria

- [ ] **Given** a ProjectCreated event is published
  - **When** the ProjectListProjection handles the event
  - **Then** a new entry is added to ProjectListReadModel
  - **And** the entry has correct initial values (status=PLANNING, totalBudget=0)

- [ ] **Given** a BudgetItemAdded event is published
  - **When** the ProjectListProjection handles the event
  - **Then** the totalBudget is recalculated for that project
  - **And** the read model is updated

- [ ] **Given** a BudgetItemRemoved event is published
  - **When** the ProjectListProjection handles the event
  - **Then** the totalBudget is recalculated (decreased)

- [ ] **Given** a ProjectMarkedAsReady event is published
  - **When** the ProjectListProjection handles the event
  - **Then** the project status is updated to "READY"

#### Technical Notes
- Projection implements EventHandler
- Projection subscribed to relevant events
- Eventual consistency acceptable

---

### User Story 2.9: Update Project Detail Projection

**As a** system
**I want** the project detail read model to stay synchronized with events
**So that** detail queries return complete information

#### Acceptance Criteria

- [ ] **Given** a ProjectCreated event is published
  - **When** the ProjectDetailProjection handles the event
  - **Then** a new entry is created in ProjectDetailReadModel
  - **And** the entry has all project details

- [ ] **Given** a BudgetItemAdded event is published
  - **When** the ProjectDetailProjection handles the event
  - **Then** the budget item is added to the embedded budget items list
  - **And** the total budget is recalculated

- [ ] **Given** a BudgetItemUpdated event is published
  - **When** the ProjectDetailProjection handles the event
  - **Then** the specific budget item is updated
  - **And** the total budget is recalculated

- [ ] **Given** a NoteAdded event is published
  - **When** the ProjectDetailProjection handles the event
  - **Then** the note is added to the embedded notes list

- [ ] **Given** a ProjectTimelineChanged event is published
  - **When** the ProjectDetailProjection handles the event
  - **Then** the timeline is updated in the read model

#### Technical Notes
- More events to handle than list projection
- Budget items stored as list in DTO

---

## Epic: CQRS Integration

### User Story 2.10: End-to-End Command to Query Flow

**As a** system
**I want** commands to update write model and queries to read from read model
**So that** CQRS pattern is fully implemented

#### Acceptance Criteria

- [ ] **Given** I execute a CreateProjectCommand
  - **When** the command completes
  - **Then** the project is persisted in event store
  - **And** ProjectCreated event is published
  - **And** ProjectListProjection updates read model
  - **And** ProjectDetailProjection updates read model
  - **And** when I query GetProjectListQuery, the new project appears
  - **And** when I query GetProjectDetailsQuery, full details are returned

- [ ] **Given** I execute AddBudgetItemCommand
  - **When** the command completes
  - **Then** the event store has BudgetItemAdded event
  - **And** projections update read models
  - **And** queries return updated budget information

#### Technical Notes
- Integration test covering full flow
- Eventual consistency: may need small delay for async processing

---

### User Story 2.11: Write-Read Separation

**As a** developer
**I want** clear separation between write and read models
**So that** they can be optimized independently

#### Acceptance Criteria

- [ ] **Given** the command side
  - **When** I execute commands
  - **Then** commands only interact with domain aggregates
  - **And** commands never read from read models
  - **And** commands return minimal data (IDs, not full DTOs)

- [ ] **Given** the query side
  - **When** I execute queries
  - **Then** queries only read from read models
  - **And** queries never load aggregates from event store
  - **And** queries never call domain methods

- [ ] **Given** I need to update a project
  - **When** I implement the use case
  - **Then** I use a command for the update
  - **And** I use a query to fetch current state for display
  - **And** the two are separate calls

#### Technical Notes
- Commands return IDs, not full objects
- Queries return DTOs
- No aggregate leakage to presentation

---

## Epic: Application Layer Configuration

### User Story 2.12: Wire Up Event Bus Subscriptions

**As a** developer
**I want** projections automatically subscribed to events
**So that** the system works out of the box

#### Acceptance Criteria

- [ ] **Given** the application starts
  - **When** configuration is loaded
  - **Then** ProjectListProjection is subscribed to relevant events
  - **And** ProjectDetailProjection is subscribed to relevant events
  - **And** any command emitting events triggers projection updates

#### Technical Notes
- Configuration class with @Bean methods
- Subscribe projections in @PostConstruct or configuration method

---

## Phase 2 Completion Criteria

### Functional
- [ ] All user stories implemented (2.1 - 2.12)
- [ ] All acceptance criteria met
- [ ] Commands execute domain operations
- [ ] Queries return data from read models
- [ ] Projections stay synchronized with events
- [ ] Event bus publishes and delivers events

### Technical
- [ ] Command handlers implemented for all operations
- [ ] Query handlers implemented (list + detail views)
- [ ] InMemoryEventBus working
- [ ] Projections handle all relevant events
- [ ] Read models separated from write models
- [ ] CQRS pattern correctly implemented

### Quality
- [ ] Integration tests pass (command → event → projection → query)
- [ ] Unit tests for handlers
- [ ] Test coverage: 85%+ on application layer
- [ ] All tests pass: `mvn verify`
- [ ] Code reviewed
- [ ] Git commit: "Phase 2 complete: CQRS with projections"

---

## Story Points Estimate

| User Story | Complexity | Points |
|------------|-----------|--------|
| 2.1 Create Project Command | Medium | 5 |
| 2.2 Add Budget Item Command | Small | 2 |
| 2.3 Multiple Commands | Medium | 5 |
| 2.4 Publish Events | Medium | 5 |
| 2.5 Subscribe to Events | Small | 3 |
| 2.6 Project List View | Medium | 5 |
| 2.7 Project Detail View | Medium | 5 |
| 2.8 Project List Projection | Large | 8 |
| 2.9 Project Detail Projection | Large | 8 |
| 2.10 End-to-End Flow | Medium | 5 |
| 2.11 Write-Read Separation | Small | 2 |
| 2.12 Wire Up Subscriptions | Small | 3 |
| **Total** | | **56** |

**Estimated Duration:** 1 week (assumes Phase 1 complete and understanding of CQRS)