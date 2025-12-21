# Initial Conversation - CCPP Architecture Discussion

**Date**: 2025-12-09
**Participants**: Joffrey (User) & Claude (AI Assistant)
**Purpose**: Define architecture and bounded contexts for CCPP

---

## Discovery Process - Building Architecture Together

### Question 1: What is a "Project Plan"?

**User's Answer**:
> "A project is a video planning, it has a timeline and status, timeline can be changed but only while planning. In fact when the project state is 'READY' nothing can changes."

**Key Insight**: State-based immutability - READY status makes project immutable.

---

### Question 2: Budget Items - Ownership

**User's Answer**:
> "A budget item is part of a Project plan, like I want to say that for this project 'Hotel 2x night cost 300$' for example, so deleting a project delete all budget"

**Key Insight**: Compositional relationship - Budget items are entities inside Project aggregate.

---

### Question 3: Brainstorm Page

**User's Answer**:
> "B workspace level"

Options were:
- A) Per-project brainstorm
- B) Workspace-level brainstorm
- C) Both

**Key Insight**: Brainstorm is separate from projects, workspace-scoped.

---

### Question 4: "Planning to Participant"

**User's Answer**:
> "Its the people not in the team of content creator, like Joyca will do a video with Mcfly, then Mcfly will receive the plan of the project"

**Key Insight**: Participants are external collaborators, not team members.

---

### Question 5: Multi-tenancy Scope

**User's Answer**:
> "A tenant is team of content creator, for example for Joyca, its all the team that works for Joyca. multiple users are part of the tenant so they all share all projects of a tenant"

**Key Insight**: Tenant = workspace = content creator team. All team members share projects.

---

### Question 6: Core Feature Priority

**User's Answer**:
> "The main feature is 'Create project with budget tracking' so that the whole team can prepare a project"

**Key Insight**: Project Planning is the CORE DOMAIN.

---

## Deep Dive Questions - Architectural Decisions

### Budget Items - Inside or Outside Aggregate?

**Claude**: "Each budget step should change total budget - this affects invariants?"

**User's Answer**:
> "I don't know, each budget steps should change total budget of project, I think its linked because project can maybe have business rules depending on budget"

**Decision**: Budget items are entities INSIDE Project aggregate because:
- Changing budget item recalculates project total
- Invariants may depend on total budget
- Strong lifecycle coupling

---

### Workspace - Full Aggregate or Just ID?

**Claude**: "Does Workspace have business rules?"

**User's Answer**:
> "I think workspace will have business rules depending on the SubscriptionTier, like freemium 2 projects, premium unlimited"

**Decision**: Workspace is FULL AGGREGATE with business rules:
- Freemium tier: max 2 projects
- Premium tier: unlimited projects
- This requires a saga to enforce limits

---

### Participants - Separate Aggregate or Inside Project?

**Claude**: "Can participants exist without a project? Can they be invited to multiple projects? Is response critical?"

**User's Answers**:
> - Can participants exist without a project? = no
> - Can a participant be invited to multiple projects? = yes
> - Is participant response critical project data or separate concern? = its critical, if participant decline we can invite another

**Decision**: Participants are entities INSIDE Project aggregate because:
- Cannot exist without project
- Response is critical to project state
- Same person can be invited to multiple projects (as separate entities per project)

---

### Notes - Simple or Complex?

**Claude**: "Are notes just text? Do they have workflow? Can team members comment?"

**User's Answers**:
> - Are notes just text attached to project? = yes
> - Or do they have workflow (draft → published)? = no
> - Can team members comment on notes? = no

**Decision**: Notes are simple value objects inside Project.

---

### Authentication - Domain or Infrastructure?

**Claude**: "Where should User/Authentication live?"

**User's Answer**:
> "I don't know. I don't think separate Identity bounded context. I think infrastructure concern"

**Decision**: Authentication is infrastructure, NOT a bounded context:
- API Gateway handles JWT authentication
- Extracts userId + workspaceId from token
- Passes to domain as context
- No domain model needed

---

### Brainstorm to Project Conversion

**User's Answer**:
> "A brainstorm can be converted to a project, using brainstorm data into the new project"

**Decision**: Cross-context saga needed:
1. User triggers conversion
2. Ideation context marks idea as converted
3. Workspace context checks subscription limits
4. Project Planning context creates project
5. Ideation context links to created project

---

## Final Bounded Contexts Agreed Upon

### 1. Workspace Context (Multi-tenancy)
- Aggregate: Workspace
- Business Rules: Subscription limits (freemium vs premium)
- Events: WorkspaceProjectCreationApproved, etc.

### 2. Ideation Context (Brainstorming)
- Aggregate: Brainstorm
- Workspace-scoped ideas
- Conversion to projects via saga

### 3. Project Planning Context (CORE DOMAIN)
- Aggregate: Project (root)
- Entities: BudgetItem, Participant
- Value Objects: Note
- Invariants:
  - Immutable when status = READY
  - Total budget = sum of budget items
  - Timeline only changeable in PLANNING

### 4. Notification Context (Supporting)
- Aggregate: Notification
- Sends emails/SMS
- Reacts to domain events

---

## Three Sagas Identified

### Saga 1: Create Project with Subscription Check
```
User → CreateProjectCommand
→ Workspace checks tier + project count
→ If allowed: WorkspaceProjectCreationApproved event
→ Project created
→ ProjectCreated event
→ Workspace project count updated
```

### Saga 2: Participant Invitation Flow
```
Project marked as READY
→ ProjectMarkedAsReady event
→ Notification sends emails to participants
→ Participant responds (Accept/Decline)
→ Project updated with response
→ If declined: may trigger replacement workflow
```

### Saga 3: Brainstorm to Project Conversion
```
User converts brainstorm to project
→ Brainstorm marked as converted
→ Workspace checks limits
→ Project created from brainstorm data
→ Brainstorm linked to project
```

---

## Multi-Tenancy Strategy

**TenantId = WorkspaceId**:
- Every aggregate has `workspaceId` field
- All queries filtered by `workspaceId`
- API Gateway extracts from JWT and injects into requests
- Domain enforces tenant isolation

**Authentication Flow**:
```
Client sends JWT
→ API Gateway validates
→ Extracts userId + workspaceId from claims
→ Adds to headers (X-User-Id, X-Workspace-Id)
→ Application layer creates commands with context
→ Domain enforces isolation
```

---

## Key Architectural Decisions Made

### Decision 1: Budget Inside Project Aggregate
**Why**: Strong lifecycle coupling + invariants depend on total budget

### Decision 2: Workspace is Full Aggregate
**Why**: Has business rules (subscription limits)

### Decision 3: Participants Inside Project
**Why**: Cannot exist without project + response is critical

### Decision 4: Notes are Value Objects
**Why**: Simple text, no behavior, no workflow

### Decision 5: Authentication is Infrastructure
**Why**: No domain logic, just technical concern

### Decision 6: Saga Choreography (not Orchestration)
**Why**: Loose coupling, decentralized, fits event-driven architecture

### Decision 7: Event Sourcing with CQRS
**Why**: Complete audit trail + optimized read models

### Decision 8: Clean Architecture Layers
**Why**: Domain independence + testability

### Decision 9: TDD Approach
**Why**: High confidence in business logic + living documentation

### Decision 10: In-Memory Adapters First
**Why**: Rapid TDD without infrastructure complexity

---

## Event Sourcing Strategy

### Event Streams
- `project-{projectId}` - All project events
- `workspace-{workspaceId}` - All workspace events
- `brainstorm-{brainstormId}` - All brainstorm events

### Event Structure
```json
{
  "eventId": "uuid",
  "eventType": "ProjectCreated",
  "aggregateId": "project-123",
  "workspaceId": "workspace-456",  // Multi-tenancy
  "version": 1,
  "timestamp": "2025-12-09T10:30:00Z",
  "data": { ... },
  "metadata": {
    "userId": "userJpaEntity-789",
    "correlationId": "corr-abc",  // Saga tracking
    "causationId": "event-xyz"     // Which event caused this
  }
}
```

### Projections (Read Models)
1. ProjectListView - Dashboard
2. ProjectDetailView - Project page
3. WorkspaceProjectCountView - Subscription limits
4. BrainstormListView - Ideas board

---

## Clean Architecture Layers

```
Presentation (API Gateway)
    ↓ depends on
Application (Command/Query Handlers)
    ↓ depends on
Domain (Aggregates, Entities, Value Objects) ← NO DEPENDENCIES
    ↑ implemented by
Infrastructure (Event Store, Repositories, Email)
```

**Dependency Rule**: Inner layers have NO knowledge of outer layers.

---

## TDD Strategy

### Test Pyramid
- 60% Unit Tests (Domain layer)
- 30% Integration Tests (Application layer)
- 10% E2E Tests (Full workflows)

### Coverage Goals
- Domain: 95%+
- Application: 85%+
- Infrastructure: 70%+
- Overall: 80%+

### TDD Workflow
```
RED → GREEN → REFACTOR
1. Write failing test
2. Minimal code to pass
3. Refactor while keeping tests green
4. Repeat
```

---

## Technology Stack Agreed

### Backend
- Java 25 + Spring Boot 4.0.0
- Maven multi-module project
- In-memory adapters initially
- Later: EventStoreDB + PostgreSQL

### Testing
- JUnit 5
- AssertJ
- Spring Boot Test

### Modules
```
ccpp/
├── shared/              (Value objects, base classes)
├── ApiGateway/          (Authentication, routing)
├── ProjectPlanning/     (Core domain)
├── TeamCollaboration/   (Workspace context)
├── ContentPlanning/     (Ideation context)
└── Notification/        (Supporting domain)
```

---

## Implementation Roadmap Phases

### Phase 1: Foundation + Project Core (Week 1-2)
- Project structure
- Project aggregate with TDD
- In-memory event store
- 20+ domain tests

### Phase 2: CQRS + Projections (Week 3)
- Command handlers
- Query handlers
- Projection updaters

### Phase 3: Workspace + Multi-Tenancy (Week 4)
- Workspace aggregate
- Subscription limits
- Tenant isolation

### Phase 4: Participant Saga (Week 5)
- Participant entities
- Invitation workflow
- Email notifications

### Phase 5: Ideation + Brainstorm (Week 6)
- Brainstorm aggregate
- Conversion saga

### Phase 6: Production Infrastructure (Week 7-8)
- Real event store
- Real database
- Real email service

### Phase 7: Testing + Documentation (Week 9-10)
- E2E tests
- Performance testing
- Documentation

---

## Key Invariants Discovered

### Project Invariants
1. Cannot modify project when status = READY (immutability)
2. Timeline can only change in PLANNING status
3. End date must be after start date
4. Total budget = sum of all budget items
5. Cannot invite same email twice to same project
6. Status can only transition PLANNING → READY (no rollback)

### Workspace Invariants
1. Freemium: max 2 projects
2. Premium: unlimited projects
3. Must have at least one owner

### Brainstorm Invariants
1. Ideas belong to workspace
2. Once converted, idea marked to prevent duplicate conversion

---

## Critical Insights from Conversation

### 1. Immutability as Business Rule
**User**: "When the project state is 'READY' nothing can changes"

This isn't just a technical choice - it's a business invariant! When a plan is sent to participants, it cannot be modified. This is domain logic, not infrastructure.

### 2. Subscription Limits Drive Architecture
**User**: "Freemium 2 projects, premium unlimited"

This business rule required:
- Workspace as full aggregate (not just ID)
- Saga choreography for project creation
- Read model to track project count
- Event-driven architecture

### 3. Participants are External
**User**: "Mcfly will receive the plan of the project"

This distinction (team members vs external participants) clarified that:
- Team members = workspace users (not modeled in domain)
- Participants = external collaborators (domain entity)
- Different concerns, different models

### 4. Budget is Compositional
**User**: "Deleting a project delete all budget"

Strong lifecycle coupling indicated:
- Budget items are NOT separate aggregate
- They're entities inside Project
- Consistency boundary is clear

### 5. Workspace = Tenant
**User**: "A tenant is team of content creator"

This clarified:
- Multi-tenancy at domain level
- All team members share projects
- No project-level privacy within workspace

---

## Collaborative Approach Worked

Instead of dumping a complete architecture, we:
1. Asked clarifying questions
2. Let userJpaEntity discover invariants
3. Explained trade-offs
4. Made decisions together
5. Validated understanding

**User's final request**:
> "Ok, now I want you to resume all this into PLAN.md, and all our conversation into INITIAL_CONVERSATION.md"

This confirms the collaborative approach was successful!

---

## What Changed from Original PLAN.md

### Removed/Changed
- ❌ TeamMember as separate bounded context → moved inside Workspace
- ❌ Budget as separate context → entities inside Project
- ❌ Content Planning context → renamed to Ideation (brainstorming)
- ❌ Missing multi-tenancy → added Workspace context

### Added
- ✅ Workspace Context with subscription business rules
- ✅ WorkspaceId (tenantId) everywhere
- ✅ Three explicit saga choreographies
- ✅ Multi-tenancy strategy
- ✅ Event sourcing with correlation/causation IDs
- ✅ Clear aggregate boundaries
- ✅ Explicit invariants
- ✅ TDD workflow
- ✅ In-memory adapters for rapid development

---

## Next Steps Agreed

1. ✅ Review plan (this conversation)
2. ⏭️ Setup project structure
3. ⏭️ Start TDD with first test: `should_create_project_with_valid_data()`
4. ⏭️ Implement Project aggregate
5. ⏭️ Build iteratively

---

## Lessons Learned

### 1. Start with Domain Understanding
Don't jump to architecture. Understand the business first.

### 2. Invariants Reveal Aggregate Boundaries
"When X changes, Y must update" → same aggregate
"X can exist without Y" → different aggregates

### 3. Business Rules Drive Technical Choices
Subscription limits → required saga
Immutability when READY → required state machine
External participants → required workflow

### 4. Multi-Tenancy is Not Optional
For SaaS apps, tenant isolation must be at domain level, not just infrastructure.

### 5. Choreography Requires Clear Events
Each saga needs well-defined events with proper correlation IDs.

### 6. Start Simple, Evolve
In-memory adapters → rapid TDD → production infrastructure later

---

## Quotes from Conversation

> "I think its linked because project can maybe have business rules depending on budget"

This shows the userJpaEntity thinking about invariants - budget affects project state!

> "Its critical, if participant decline we can invite another"

This shows participant response is part of the workflow, not just data.

> "Freemium 2 projects, premium unlimited"

Clear business rule that drives architectural decisions.

> "When the project state is 'READY' nothing can changes"

Perfect example of domain invariant becoming code.

---

## Architecture Validation

### DDD Principles ✅
- Clear bounded contexts
- Ubiquitous language (Project, Workspace, Participant, etc.)
- Aggregates protect invariants
- Rich domain models (not anemic)

### Event Sourcing ✅
- Events as source of truth
- Complete audit trail
- Projections for queries
- Temporal queries possible

### CQRS ✅
- Commands for writes
- Queries for reads
- Separate models optimized for each

### Clean Architecture ✅
- Domain has zero dependencies
- Infrastructure implements domain interfaces
- Business logic testable

### Event-Driven ✅
- Saga choreography
- Loose coupling between contexts
- Event bus for integration

### TDD ✅
- Test-first approach
- High coverage goals
- Tests as documentation

---

## Final Architecture Summary

**4 Bounded Contexts**:
1. Workspace (multi-tenancy + subscriptions)
2. Ideation (workspace-level brainstorming)
3. Project Planning (CORE - video planning with budget)
4. Notification (supporting - emails/SMS)

**3 Sagas**:
1. Create Project (Workspace → Project)
2. Participant Invitation (Project → Notification → Project)
3. Brainstorm Conversion (Ideation → Workspace → Project)

**Core Aggregate**:
- Project (root)
  - BudgetItem (entity)
  - Participant (entity)
  - Note (value object)

**Multi-Tenancy**:
- WorkspaceId in all aggregates
- All queries filtered by tenant
- API Gateway injects context

**Event Sourcing**:
- Event streams per aggregate
- Projections for read models
- Correlation IDs for saga tracking

---

**Conversation End**
**Status**: Architecture defined, ready to implement
**Next**: Create project structure and start TDD with Project aggregate
