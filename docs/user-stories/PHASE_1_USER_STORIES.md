# Phase 1: Foundation + Project Core - User Stories

## Overview
Phase 1 establishes the foundation of the CCPP platform with the core Project aggregate, implementing Clean Architecture, DDD principles, Event Sourcing, and TDD.

---

## Epic: Project Management Core

### User Story 1.1: Create Video Project

**As a** content creator
**I want to** create a video project with title, description, and timeline
**So that** I can start planning my video production

#### Acceptance Criteria

- [x] **Given** I am a content creator in a workspace
  - **When** I create a project with valid data (title, description, timeline)
  - **Then** the project is created with status "PLANNING"
  - **And** a unique ProjectId is generated
  - **And** the project belongs to the workspace (workspaceId is set)
  - **And** a ProjectCreated event is emitted

- [ ] **Given** I create a project
  - **When** I provide a timeline with end date before start date
  - **Then** the system rejects the project creation
  - **And** returns an "InvalidTimelineException"

- [ ] **Given** I create a project
  - **When** I provide an empty title
  - **Then** the system rejects the project creation
  - **And** returns a validation error

#### Technical Notes
- Project must be an aggregate root
- Timeline must be a value object with validation
- Events must be stored in event store
- WorkspaceId must be provided (tenant isolation)

---

### User Story 1.2: Add Budget Items to Project

**As a** content creator
**I want to** add budget items with description and amount
**So that** I can track project expenses and calculate total budget

#### Acceptance Criteria

- [x] **Given** I have a project in PLANNING status
  - **When** I add a budget item with description "Hotel 2x nights" and amount $300
  - **Then** the budget item is added to the project
  - **And** a BudgetItemAdded event is emitted
  - **And** the budget item has a unique BudgetItemId

- [x] **Given** I have added multiple budget items
  - **When** I request the total budget
  - **Then** the system calculates the sum of all budget item amounts
  - **And** returns the correct total (e.g., $300 + $150 = $450)

- [x] **Given** I have a project in PLANNING status
  - **When** I add a budget item with negative amount
  - **Then** the system rejects the operation
  - **And** returns a validation error

- [x] **Given** I have a project in PLANNING status
  - **When** I add a budget item with different currency than existing items
  - **Then** the system rejects the operation
  - **And** returns a "CurrencyMismatchException"

#### Technical Notes
- BudgetItem is an entity within Project aggregate
- Money must be a value object with currency validation
- Total budget calculated via domain logic, not stored

---

### User Story 1.3: Update Budget Items

**As a** content creator
**I want to** update existing budget items
**So that** I can correct mistakes or adjust estimates

#### Acceptance Criteria

- [x] **Given** I have a project with budget items
  - **When** I update a budget item's description or amount
  - **Then** the budget item is updated
  - **And** a BudgetItemUpdated event is emitted
  - **And** the total budget is recalculated

- [x] **Given** I have a project with budget items
  - **When** I attempt to update a non-existent budget item
  - **Then** the system rejects the operation
  - **And** returns a "BudgetItemNotFoundException"

---

### User Story 1.4: Remove Budget Items

**As a** content creator
**I want to** remove budget items that are no longer relevant
**So that** my budget reflects current reality

#### Acceptance Criteria

- [x] **Given** I have a project with budget items in PLANNING status
  - **When** I remove a budget item
  - **Then** the budget item is removed from the project
  - **And** a BudgetItemRemoved event is emitted
  - **And** the total budget is recalculated

- [x] **Given** I have a project with budget items
  - **When** I attempt to remove a non-existent budget item
  - **Then** the system rejects the operation
  - **And** returns a "BudgetItemNotFoundException"

---

### User Story 1.5: Add Notes to Project

**As a** content creator
**I want to** add notes to my project
**So that** I can document ideas, decisions, and important information

#### Acceptance Criteria

- [x] **Given** I have a project
  - **When** I add a note with content
  - **Then** the note is added to the project
  - **And** a NoteAdded event is emitted
  - **And** the note includes timestamp and author

- [x] **Given** I have a project
  - **When** I add an empty note
  - **Then** the system rejects the operation
  - **And** returns a validation error

#### Technical Notes
- Note is a value object (content, createdAt, createdBy)
- Notes are immutable (no update/delete in Phase 1)

---

### User Story 1.6: Mark Project as READY

**As a** content creator
**I want to** mark my project as READY
**So that** I can signal the planning phase is complete and execution can begin

#### Acceptance Criteria

- [x] **Given** I have a project in PLANNING status
  - **When** I mark the project as READY
  - **Then** the project status changes to READY
  - **And** a ProjectMarkedAsReady event is emitted
  - **And** the readyAt timestamp is recorded

- [x] **Given** I have a project already in READY status
  - **When** I attempt to mark it as READY again
  - **Then** the system does nothing

#### Technical Notes
- Status transition: PLANNING → READY (one-way, no rollback)
- Event must include project details for downstream sagas

---

### User Story 1.7: Prevent Modifications to READY Projects

**As a** content creator
**I want** projects to become immutable once marked as READY
**So that** the approved plan cannot be accidentally changed

#### Acceptance Criteria

- [ ] **Given** I have a project in READY status
  - **When** I attempt to change the timeline
  - **Then** the system rejects the operation
  - **And** returns a "CannotModifyReadyProjectException"

- [ ] **Given** I have a project in READY status
  - **When** I attempt to add a budget item
  - **Then** the system rejects the operation
  - **And** returns a "CannotModifyReadyProjectException"

- [ ] **Given** I have a project in READY status
  - **When** I attempt to update project details
  - **Then** the system rejects the operation
  - **And** returns a "CannotModifyReadyProjectException"

- [ ] **Given** I have a project in PLANNING status
  - **When** I change the timeline
  - **Then** the timeline is updated successfully
  - **And** a ProjectTimelineChanged event is emitted

#### Technical Notes
- Immutability enforced in domain logic
- All mutating methods must check status first

---

### User Story 1.8: Change Project Timeline

**As a** content creator
**I want to** change the project timeline while in PLANNING
**So that** I can adjust dates as my schedule changes

#### Acceptance Criteria

- [ ] **Given** I have a project in PLANNING status
  - **When** I change the timeline to valid dates
  - **Then** the timeline is updated
  - **And** a ProjectTimelineChanged event is emitted

- [ ] **Given** I have a project in PLANNING status
  - **When** I change the timeline with end date before start date
  - **Then** the system rejects the operation
  - **And** returns an "InvalidTimelineException"

- [ ] **Given** I have a project in READY status
  - **When** I attempt to change the timeline
  - **Then** the system rejects the operation (see User Story 1.7)

---

## Epic: Event Sourcing Infrastructure

### User Story 1.9: Store Domain Events

**As a** developer
**I want to** store all domain events in an event store
**So that** I have a complete audit trail and can rebuild aggregate state

#### Acceptance Criteria

- [ ] **Given** a domain aggregate emits events
  - **When** the aggregate is saved
  - **Then** all uncommitted events are appended to the event store
  - **And** events are stored in a stream identified by aggregate ID
  - **And** the aggregate's events are cleared after saving

- [ ] **Given** events exist in the event store
  - **When** I read the event stream for an aggregate
  - **Then** all events for that aggregate are returned in order

- [ ] **Given** I attempt to append events with wrong version
  - **When** optimistic concurrency check fails
  - **Then** the system throws a "ConcurrencyException"

#### Technical Notes
- In-memory implementation for Phase 1
- Stream ID format: "project-{projectId}"
- Events must include version for optimistic concurrency

---

### User Story 1.10: Rebuild Aggregate from Events

**As a** developer
**I want to** rebuild aggregate state from events
**So that** I can load aggregates without traditional database queries

#### Acceptance Criteria

- [ ] **Given** events exist in the event store for a project
  - **When** I load the project by ID
  - **Then** the system reads all events from the stream
  - **And** replays events to rebuild the aggregate state
  - **And** the aggregate's current state matches the event history

- [ ] **Given** no events exist for a project ID
  - **When** I attempt to load the project
  - **Then** the system returns empty Optional

- [ ] **Given** a project with 10 events (created, 5 budget items added, 3 updated, marked ready)
  - **When** I rebuild the project from events
  - **Then** the project has correct status (READY)
  - **And** the project has 5 budget items
  - **And** the total budget is correct

#### Technical Notes
- EventSourcedProjectRepository implementation
- Aggregate must have event replay logic
- All events must be applied in order

---

## Epic: Clean Architecture Setup

### User Story 1.11: Shared Kernel - Value Objects

**As a** developer
**I want** reusable value objects in a shared module
**So that** all bounded contexts can use consistent domain primitives

#### Acceptance Criteria

- [ ] **Given** I need to represent money
  - **When** I create a Money value object with amount and currency
  - **Then** the money value is immutable
  - **And** I can add two money values with same currency
  - **And** adding different currencies throws an exception

- [ ] **Given** I need to represent a date range
  - **When** I create a DateRange with start and end dates
  - **Then** the date range validates end date is after start date
  - **And** invalid ranges throw an exception

- [ ] **Given** I need to represent an email
  - **When** I create an Email value object
  - **Then** the email is validated (basic format check)
  - **And** invalid emails throw an exception

- [ ] **Given** I create two value objects with same values
  - **When** I compare them
  - **Then** they are equal (value equality)

#### Technical Notes
- Value objects are immutable
- Validation in constructor
- Override equals() and hashCode()

---

### User Story 1.12: Domain Layer Independence

**As a** developer
**I want** the domain layer to have zero framework dependencies
**So that** business logic is pure and testable

#### Acceptance Criteria

- [ ] **Given** the domain module
  - **When** I inspect dependencies
  - **Then** there are no Spring, JPA, or infrastructure dependencies
  - **And** only Java standard library and shared kernel are used

- [ ] **Given** domain tests
  - **When** I run tests
  - **Then** they execute without Spring context
  - **And** tests run in milliseconds (fast)

#### Technical Notes
- Clean Architecture: domain at center, no inward dependencies
- Use interfaces (ports) for infrastructure concerns

---

## Phase 1 Completion Criteria

### Functional
- [ ] All user stories implemented (1.1 - 1.12)
- [ ] All acceptance criteria met
- [ ] Can create, modify, and mark projects as READY
- [ ] Budget items can be added, updated, removed
- [ ] Total budget calculated correctly
- [ ] Timeline immutability enforced
- [ ] Notes can be added

### Technical
- [ ] 20+ unit tests written (TDD)
- [ ] Test coverage: 95%+ on domain layer
- [ ] Event store stores and retrieves events
- [ ] Aggregates can be rebuilt from events
- [ ] Clean Architecture maintained (no framework in domain)
- [ ] Value objects immutable and validated

### Quality
- [ ] All tests pass: `mvn test`
- [ ] No code smells
- [ ] Code reviewed
- [ ] Git commit: "Phase 1 complete: Project aggregate with event sourcing"

---

## Story Points Estimate

| User Story | Complexity | Points |
|------------|-----------|--------|
| 1.1 Create Project | Medium | 5 |
| 1.2 Add Budget Items | Medium | 5 |
| 1.3 Update Budget Items | Small | 2 |
| 1.4 Remove Budget Items | Small | 2 |
| 1.5 Add Notes | Small | 2 |
| 1.6 Mark as READY | Small | 3 |
| 1.7 Prevent Modifications | Medium | 3 |
| 1.8 Change Timeline | Small | 2 |
| 1.9 Store Events | Large | 8 |
| 1.10 Rebuild from Events | Large | 8 |
| 1.11 Value Objects | Medium | 5 |
| 1.12 Domain Independence | Small | 2 |
| **Total** | | **47** |

**Estimated Duration:** 1-2 weeks (depends on experience with DDD/Event Sourcing)
