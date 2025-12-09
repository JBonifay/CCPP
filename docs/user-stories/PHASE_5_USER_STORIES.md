# Phase 5: Ideation + Brainstorm - User Stories

## Overview
Phase 5 implements the Ideation bounded context for workspace-level brainstorming, with idea-to-project conversion saga.

---

## Epic: Brainstorm Management

### User Story 5.1: Create Brainstorm for Workspace

**As a** content creator
**I want to** create a brainstorm board for my workspace
**So that** I can collect and organize video ideas before creating projects

#### Acceptance Criteria

- [ ] **Given** I am in a workspace
  - **When** I create a brainstorm board
  - **Then** a Brainstorm aggregate is created
  - **And** it belongs to my workspace (workspaceId)
  - **And** initial idea count is 0
  - **And** status is ACTIVE
  - **And** a BrainstormCreated event is emitted

#### Technical Notes
- Brainstorm is an aggregate root
- WorkspaceId for tenant isolation
- ConversionStatus: ACTIVE, CONVERTED_TO_PROJECT

---

### User Story 5.2: Add Ideas to Brainstorm

**As a** content creator
**I want to** add ideas to my brainstorm board
**So that** I can capture video concepts as they come to me

#### Acceptance Criteria

- [ ] **Given** I have a brainstorm board
  - **When** I add an idea with title "Behind the scenes vlog" and description
  - **Then** the idea is added to the brainstorm
  - **And** the idea has a unique IdeaId
  - **And** the idea has createdAt timestamp
  - **And** the idea's converted flag is false
  - **And** an IdeaAdded event is emitted

- [ ] **Given** I add an idea with empty title
  - **When** validation occurs
  - **Then** the system rejects the operation

#### Technical Notes
- Idea is an entity within Brainstorm aggregate
- Ideas are workspace-level, not project-specific

---

### User Story 5.3: Update Ideas

**As a** content creator
**I want to** update idea titles and descriptions
**So that** I can refine concepts over time

#### Acceptance Criteria

- [ ] **Given** I have an idea in my brainstorm
  - **When** I update its title or description
  - **Then** the idea is updated
  - **And** an IdeaUpdated event is emitted

---

### User Story 5.4: Mark Idea as Converted

**As a** system
**I want** to mark ideas as converted when they become projects
**So that** I prevent duplicate conversions

#### Acceptance Criteria

- [ ] **Given** I have an unconverted idea
  - **When** I convert it to a project
  - **Then** the idea's converted flag is set to true
  - **And** a BrainstormIdeaConverted event is emitted with idea data

- [ ] **Given** I have an already converted idea
  - **When** I attempt to convert it again
  - **Then** the system rejects the operation
  - **And** returns "IdeaAlreadyConvertedException"

#### Technical Notes
- convertIdeaToProject() method
- Returns IdeaData (title, description) for project creation

---

### User Story 5.5: Link Brainstorm to Created Project

**As a** system
**I want** to link brainstorms to projects created from them
**So that** I can track which ideas became reality

#### Acceptance Criteria

- [ ] **Given** a project is created from an idea
  - **When** the project creation completes
  - **Then** the brainstorm is linked to the projectId
  - **And** a BrainstormLinkedToProject event is emitted

#### Technical Notes
- linkToProject() method
- Stores ProjectId reference

---

## Epic: Ideation Read Models

### User Story 5.6: Brainstorm List View

**As a** content creator
**I want to** view all brainstorm boards in my workspace
**So that** I can access my idea collections

#### Acceptance Criteria

- [ ] **Given** I have created 3 brainstorm boards
  - **When** I query for brainstorm list
  - **Then** I receive a list of BrainstormListDTO objects
  - **And** each includes: brainstormId, workspaceId, ideaCount, convertedCount, updatedAt
  - **And** only my workspace's brainstorms are returned

---

## Epic: Idea-to-Project Conversion Saga

### User Story 5.7: Convert Idea to Project Command

**As a** content creator
**I want to** convert a brainstorm idea into a full project
**So that** I can move from ideation to execution

#### Acceptance Criteria

- [ ] **Given** I have an unconverted idea
  - **When** I execute ConvertBrainstormToProjectCommand
  - **Then** the idea is marked as converted
  - **And** BrainstormIdeaConverted event is emitted with idea title and description

#### Technical Notes
- ConvertBrainstormToProjectCommand(brainstormId, ideaId, workspaceId, userId)
- First step of saga

---

### User Story 5.8: Create Project from Brainstorm Idea

**As a** system
**I want** projects automatically created when ideas are converted
**So that** the conversion process is seamless

#### Acceptance Criteria

- [ ] **Given** a BrainstormIdeaConverted event is published
  - **When** ProjectCreationFromBrainstormSaga handles the event
  - **Then** it checks workspace subscription limits (same as CreateProject)
  - **And** if approved, creates Project aggregate with idea's title and description
  - **And** project has special marker: createdFromBrainstormId
  - **And** ProjectCreatedFromBrainstorm event is emitted

- [ ] **Given** workspace is at project limit
  - **When** idea conversion is attempted
  - **Then** ProjectLimitReachedException is thrown
  - **And** idea remains unconverted (compensating action)

#### Technical Notes
- Saga integrates with Workspace subscription check
- Project.createFromBrainstorm() factory method
- Subscription limits still enforced

---

### User Story 5.9: Link Brainstorm After Project Creation

**As a** system
**I want** brainstorms linked to projects after successful creation
**So that** the relationship is tracked

#### Acceptance Criteria

- [ ] **Given** a ProjectCreatedFromBrainstorm event is published
  - **When** BrainstormLinkingSaga handles the event
  - **Then** it loads the brainstorm aggregate
  - **And** links it to the created projectId
  - **And** BrainstormLinkedToProject event is emitted

#### Technical Notes
- Completes the saga circle
- Final step in conversion flow

---

### User Story 5.10: Complete Conversion Saga Flow

**As a** content creator
**I want** the full conversion process to work end-to-end
**So that** converting ideas is simple and automatic

#### Acceptance Criteria

- [ ] **Given** I have a brainstorm with an idea in a freemium workspace with 1 project
  - **When** I execute ConvertBrainstormToProjectCommand
  - **Then** the idea is marked as converted
  - **And** a new project is created with the idea's title and description
  - **And** the brainstorm is linked to the new project
  - **And** workspace project count is incremented to 2
  - **And** event chain is complete: BrainstormIdeaConverted → WorkspaceProjectCreationApproved → ProjectCreatedFromBrainstorm → BrainstormLinkedToProject

- [ ] **Given** I have a freemium workspace with 2 projects (at limit)
  - **When** I attempt to convert an idea
  - **Then** conversion fails with ProjectLimitReachedException
  - **And** the idea remains unconverted

#### Technical Notes
- Integration test covering complete saga
- Verifies all events in chain
- Tests failure scenarios

---

## Epic: Brainstorm Commands and Repository

### User Story 5.11: Brainstorm Command Handlers

**As a** developer
**I want** command handlers for brainstorm operations
**So that** operations follow application layer patterns

#### Acceptance Criteria

- [ ] **Given** brainstorm operations
  - **When** implementing command handlers
  - **Then** CreateBrainstormCommandHandler is implemented
  - **And** AddIdeaCommandHandler is implemented
  - **And** UpdateIdeaCommandHandler is implemented
  - **And** all follow standard patterns

---

### User Story 5.12: Brainstorm Event-Sourced Repository

**As a** developer
**I want** event-sourced brainstorm repository
**So that** brainstorms persist with full audit trail

#### Acceptance Criteria

- [ ] **Given** brainstorm events
  - **When** brainstorm is saved
  - **Then** events are appended to stream "brainstorm-{brainstormId}"
  - **And** brainstorm can be rebuilt from events

---

## Phase 5 Completion Criteria

### Functional
- [ ] All user stories implemented (5.1 - 5.12)
- [ ] All acceptance criteria met
- [ ] Brainstorms can be created at workspace level
- [ ] Ideas can be added and updated
- [ ] Ideas can be converted to projects
- [ ] Conversion saga works end-to-end
- [ ] Subscription limits enforced during conversion
- [ ] Brainstorms linked to created projects

### Technical
- [ ] Brainstorm aggregate implemented
- [ ] Idea entity within aggregate
- [ ] Ideation read models working
- [ ] ProjectCreationFromBrainstormSaga working
- [ ] BrainstormLinkingSaga working
- [ ] Event chain complete
- [ ] Repository with event sourcing

### Quality
- [ ] Integration test for conversion saga
- [ ] Unit tests for aggregates and sagas
- [ ] Test coverage: 85%+
- [ ] All tests pass: `mvn verify`
- [ ] Code reviewed
- [ ] Git commit: "Phase 5 complete: Ideation context with conversion saga"

---

## Story Points Estimate

| User Story | Complexity | Points |
|------------|-----------|--------|
| 5.1 Create Brainstorm | Small | 3 |
| 5.2 Add Ideas | Small | 3 |
| 5.3 Update Ideas | Small | 2 |
| 5.4 Mark Converted | Small | 3 |
| 5.5 Link to Project | Small | 2 |
| 5.6 Brainstorm List View | Medium | 5 |
| 5.7 Convert Command | Small | 3 |
| 5.8 Create Project Saga | Large | 8 |
| 5.9 Link Brainstorm Saga | Medium | 5 |
| 5.10 Complete Saga Flow | Large | 8 |
| 5.11 Command Handlers | Small | 3 |
| 5.12 Repository | Medium | 5 |
| **Total** | | **50** |

**Estimated Duration:** 1 week (assumes Phases 1-4 complete)