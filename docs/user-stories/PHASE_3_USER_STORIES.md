# Phase 3: Workspace + Multi-Tenancy - User Stories

## Overview
Phase 3 implements the Workspace bounded context with subscription-based business rules and enforces multi-tenant data isolation throughout the system.

---

## Epic: Workspace Management

### User Story 3.1: Create Workspace

**As a** content creator team
**I want to** create a workspace for my organization
**So that** I can start planning video projects with my team

#### Acceptance Criteria

- [ ] **Given** I am registering as a new team
  - **When** I create a workspace with a name
  - **Then** the workspace is created with a unique WorkspaceId
  - **And** the workspace has FREEMIUM subscription tier by default
  - **And** the project count is initialized to 0
  - **And** a WorkspaceCreated event is emitted

- [ ] **Given** I create a workspace
  - **When** I provide an empty workspace name
  - **Then** the system rejects the operation
  - **And** returns a validation error

#### Technical Notes
- Workspace is an aggregate root
- WorkspaceId is the tenant identifier
- Default subscription tier: FREEMIUM

---

### User Story 3.2: Freemium Subscription Limits

**As a** platform provider
**I want** freemium workspaces limited to 2 projects
**So that** we incentivize premium subscriptions

#### Acceptance Criteria

- [ ] **Given** I have a freemium workspace with 0 projects
  - **When** I request to create a project
  - **Then** the workspace approves the creation
  - **And** a WorkspaceProjectCreationApproved event is emitted

- [ ] **Given** I have a freemium workspace with 1 project
  - **When** I request to create a second project
  - **Then** the workspace approves the creation
  - **And** a WorkspaceProjectCreationApproved event is emitted

- [ ] **Given** I have a freemium workspace with 2 projects
  - **When** I request to create a third project
  - **Then** the workspace rejects the creation
  - **And** a WorkspaceProjectLimitReached event is emitted
  - **And** the system returns "ProjectLimitReachedException"

#### Technical Notes
- Limit checked in Workspace.approveProjectCreation()
- Current project count read from projection (eventual consistency acceptable)
- Business rule: FREEMIUM → max 2 projects

---

### User Story 3.3: Premium Unlimited Projects

**As a** premium subscriber
**I want** unlimited project creation
**So that** I can manage as many video projects as needed

#### Acceptance Criteria

- [ ] **Given** I have a premium workspace with 10 projects
  - **When** I request to create another project
  - **Then** the workspace approves the creation
  - **And** no limit is enforced

- [ ] **Given** I have a premium workspace with 100 projects
  - **When** I request to create another project
  - **Then** the workspace still approves the creation

#### Technical Notes
- Business rule: PREMIUM → unlimited projects
- Same approveProjectCreation() method, different logic path

---

### User Story 3.4: Upgrade Subscription

**As a** content creator
**I want to** upgrade from freemium to premium
**So that** I can create unlimited projects

#### Acceptance Criteria

- [ ] **Given** I have a freemium workspace at the 2-project limit
  - **When** I upgrade to premium
  - **Then** the subscription tier changes to PREMIUM
  - **And** a WorkspaceSubscriptionUpgraded event is emitted
  - **And** I can now create additional projects

- [ ] **Given** I have a premium workspace
  - **When** I attempt to upgrade again
  - **Then** the operation is idempotent (no error, no duplicate event)

#### Technical Notes
- upgradeSubscription() method on Workspace
- One-way upgrade (no downgrade in Phase 3)

---

## Epic: Workspace Projections

### User Story 3.5: Workspace Project Count Read Model

**As a** system
**I want** to track project count per workspace
**So that** subscription limits can be enforced

#### Acceptance Criteria

- [ ] **Given** a ProjectCreated event is published
  - **When** the WorkspaceProjectCountProjection handles the event
  - **Then** the project count for that workspace is incremented
  - **And** the read model stores: workspaceId, projectCount, subscriptionTier, maxProjects

- [ ] **Given** a WorkspaceSubscriptionUpgraded event is published
  - **When** the projection handles the event
  - **Then** the subscriptionTier is updated to PREMIUM
  - **And** maxProjects is updated to unlimited (-1 or MAX_INT)

- [ ] **Given** a new workspace is created
  - **When** WorkspaceCreated event is handled
  - **Then** the read model is initialized with projectCount = 0

#### Technical Notes
- Separate projection for workspace metrics
- Used by command handlers to check limits
- Eventually consistent (acceptable delay)

---

## Epic: Project Creation Saga with Subscription Check

### User Story 3.6: Create Project with Limit Check

**As a** content creator
**I want** project creation to automatically check my subscription limits
**So that** I know immediately if I've reached my plan's limit

#### Acceptance Criteria

- [ ] **Given** I have a freemium workspace with 1 project
  - **When** I execute CreateProjectCommand
  - **Then** the system reads current project count from read model
  - **And** calls Workspace.approveProjectCreation()
  - **And** workspace approves (under limit)
  - **And** Project aggregate is created
  - **And** WorkspaceProjectCreationApproved and ProjectCreated events are emitted
  - **And** ProjectId is returned

- [ ] **Given** I have a freemium workspace with 2 projects
  - **When** I execute CreateProjectCommand
  - **Then** the system reads project count (2)
  - **And** calls Workspace.approveProjectCreation()
  - **And** workspace rejects (at limit)
  - **And** ProjectLimitReachedException is thrown
  - **And** no Project aggregate is created
  - **And** no ProjectCreated event is emitted

- [ ] **Given** I have a premium workspace with 5 projects
  - **When** I execute CreateProjectCommand
  - **Then** workspace approves (no limit)
  - **And** project is created successfully

#### Technical Notes
- Enhanced CreateProjectCommandHandler
- Saga pattern: Workspace approval → Project creation
- Atomic operation (both or neither)

---

### User Story 3.7: Project Count Updates After Creation

**As a** system
**I want** project count automatically updated after project creation
**So that** limits are enforced accurately

#### Acceptance Criteria

- [ ] **Given** I create a project in a workspace with 0 projects
  - **When** ProjectCreated event is published
  - **Then** WorkspaceProjectCountProjection increments count to 1
  - **And** when I try to create another project, count is read as 1

- [ ] **Given** I create a project in a workspace with 1 project
  - **When** ProjectCreated event is published
  - **Then** count is incremented to 2
  - **And** freemium workspace is now at limit

#### Technical Notes
- Eventually consistent: small delay acceptable
- Integration test may need await() for async processing

---

## Epic: Multi-Tenant Data Isolation

### User Story 3.8: Workspace-Scoped Project Queries

**As a** content creator
**I want to** only see projects from my workspace
**So that** my data is private and isolated

#### Acceptance Criteria

- [ ] **Given** Workspace A has 3 projects and Workspace B has 2 projects
  - **When** a user from Workspace A queries for project list
  - **Then** only the 3 projects from Workspace A are returned
  - **And** Workspace B's projects are not visible

- [ ] **Given** I am authenticated with Workspace A
  - **When** I query GetProjectDetailsQuery for a project from Workspace B
  - **Then** the system denies access
  - **And** returns "ForbiddenException" or empty result

#### Technical Notes
- WorkspaceId filter on all queries
- Security check in query handlers
- No cross-workspace data leakage

---

### User Story 3.9: Workspace-Scoped Commands

**As a** content creator
**I want** all commands to operate within my workspace
**So that** I cannot accidentally modify other workspaces' data

#### Acceptance Criteria

- [ ] **Given** I am authenticated with Workspace A
  - **When** I send a command (e.g., AddBudgetItemCommand) for a project
  - **Then** the system validates the project belongs to Workspace A
  - **And** the command executes successfully

- [ ] **Given** I am authenticated with Workspace A
  - **When** I send a command for a project in Workspace B
  - **Then** the system rejects the operation
  - **And** returns "ForbiddenException"

#### Technical Notes
- WorkspaceId validation in all command handlers
- Compare project.workspaceId with authenticated user's workspaceId

---

### User Story 3.10: Security Context with WorkspaceId

**As a** developer
**I want** authenticated user's workspaceId available in security context
**So that** commands and queries can enforce tenant isolation

#### Acceptance Criteria

- [ ] **Given** a user authenticates (simulated for Phase 3)
  - **When** the security context is set
  - **Then** SecurityContext contains UserId and WorkspaceId
  - **And** command handlers can access SecurityContext.getContext()

- [ ] **Given** a command is executed
  - **When** the handler needs workspaceId
  - **Then** it reads from SecurityContext
  - **And** uses it for validation and filtering

#### Technical Notes
- ThreadLocal-based SecurityContext
- Real JWT implementation in Phase 6
- For Phase 3: set context in tests

---

## Epic: Workspace Integration

### User Story 3.11: Workspace Command Handlers

**As a** developer
**I want** command handlers for workspace operations
**So that** workspaces can be created and managed via application layer

#### Acceptance Criteria

- [ ] **Given** workspace operations
  - **When** I implement command handlers
  - **Then** CreateWorkspaceCommandHandler is implemented
  - **And** UpgradeWorkspaceSubscriptionCommandHandler is implemented
  - **And** both follow same patterns as project handlers

---

### User Story 3.12: Workspace Repository

**As a** developer
**I want** event-sourced workspace repository
**So that** workspaces persist consistently with projects

#### Acceptance Criteria

- [ ] **Given** workspace events
  - **When** workspace is saved
  - **Then** events are appended to event store in stream "workspace-{workspaceId}"
  - **And** workspace can be rebuilt from events

- [ ] **Given** workspace events in store
  - **When** workspace is loaded by ID
  - **Then** events are replayed to rebuild state
  - **And** subscription tier and project limits are correct

#### Technical Notes
- Similar to EventSourcedProjectRepository
- Workspace event replay logic

---

## Phase 3 Completion Criteria

### Functional
- [ ] All user stories implemented (3.1 - 3.12)
- [ ] All acceptance criteria met
- [ ] Workspaces can be created
- [ ] Subscription tiers enforced (freemium: 2, premium: unlimited)
- [ ] Project creation saga checks limits
- [ ] Multi-tenant data isolation enforced
- [ ] Cross-workspace access denied

### Technical
- [ ] Workspace aggregate implemented
- [ ] Workspace projections updating
- [ ] Security context with workspaceId
- [ ] All queries filtered by workspace
- [ ] All commands validate workspace ownership
- [ ] Saga: approval → project creation

### Quality
- [ ] Integration tests for limit enforcement
- [ ] Integration tests for multi-tenant isolation
- [ ] Unit tests for workspace aggregate
- [ ] Test coverage: 85%+
- [ ] All tests pass: `mvn verify`
- [ ] Code reviewed
- [ ] Git commit: "Phase 3 complete: Workspace context with multi-tenancy"

---

## Story Points Estimate

| User Story | Complexity | Points |
|------------|-----------|--------|
| 3.1 Create Workspace | Small | 3 |
| 3.2 Freemium Limits | Medium | 5 |
| 3.3 Premium Unlimited | Small | 2 |
| 3.4 Upgrade Subscription | Small | 3 |
| 3.5 Project Count Read Model | Medium | 5 |
| 3.6 Create Project with Check | Large | 8 |
| 3.7 Project Count Updates | Small | 3 |
| 3.8 Workspace-Scoped Queries | Medium | 5 |
| 3.9 Workspace-Scoped Commands | Medium | 5 |
| 3.10 Security Context | Medium | 5 |
| 3.11 Workspace Handlers | Small | 3 |
| 3.12 Workspace Repository | Medium | 5 |
| **Total** | | **52** |

**Estimated Duration:** 1 week (assumes Phases 1-2 complete)