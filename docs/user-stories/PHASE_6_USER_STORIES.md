# Phase 6: Production Infrastructure - User Stories

## Overview
Phase 6 replaces in-memory implementations with production-ready infrastructure: PostgreSQL for read models, EventStoreDB for event sourcing, real email service, and JWT authentication.

---

## Epic: PostgreSQL Integration

### User Story 6.1: PostgreSQL for Read Models

**As a** developer
**I want** read models persisted in PostgreSQL
**So that** data survives application restarts

#### Acceptance Criteria

- [ ] **Given** PostgreSQL is running
  - **When** I execute a CreateProjectCommand
  - **Then** the project list view is stored in PostgreSQL
  - **And** when application restarts, the data persists

- [ ] **Given** I query projects after restart
  - **When** GetProjectListQueryHandler executes
  - **Then** data is retrieved from PostgreSQL
  - **And** returns correct projects

#### Technical Notes
- Replace InMemoryProjectListReadModel with PostgresProjectListReadModel
- JPA entities for read models
- Database schema via Flyway migrations

---

### User Story 6.2: All Read Models in PostgreSQL

**As a** developer
**I want** all read models using PostgreSQL
**So that** the entire query side is production-ready

#### Acceptance Criteria

- [ ] **Given** all read models
  - **When** I replace in-memory implementations
  - **Then** ProjectDetailReadModel uses PostgreSQL
  - **And** WorkspaceProjectCountReadModel uses PostgreSQL
  - **And** BrainstormListReadModel uses PostgreSQL
  - **And** all queries work correctly

---

## Epic: EventStoreDB Integration

### User Story 6.3: EventStoreDB for Event Storage

**As a** developer
**I want** events persisted in EventStoreDB
**So that** I have production-grade event sourcing

#### Acceptance Criteria

- [ ] **Given** EventStoreDB is running
  - **When** I save a Project aggregate
  - **Then** events are appended to EventStoreDB stream
  - **And** events persist across restarts

- [ ] **Given** events are in EventStoreDB
  - **When** I load a Project by ID
  - **Then** events are read from EventStoreDB
  - **And** aggregate state is correctly rebuilt

#### Technical Notes
- EventStoreDB client library
- EventStoreDbEventStore adapter
- Event serialization/deserialization
- Stream naming: "project-{id}", "workspace-{id}", "brainstorm-{id}"

---

### User Story 6.4: Event Replay from EventStoreDB

**As a** developer
**I want** aggregates rebuilt from EventStoreDB streams
**So that** event sourcing works in production

#### Acceptance Criteria

- [ ] **Given** a project with 10 events in EventStoreDB
  - **When** I load the project
  - **Then** all 10 events are read
  - **And** events are replayed in order
  - **And** aggregate state matches event history
  - **And** query execution time is acceptable (< 100ms for typical project)

---

## Epic: Real Email Service

### User Story 6.5: SendGrid Email Integration

**As a** system
**I want** to send real emails via SendGrid
**So that** participants receive actual invitation emails

#### Acceptance Criteria

- [ ] **Given** SendGrid API key is configured
  - **When** a notification is sent
  - **Then** SendGridEmailService sends the email via SendGrid API
  - **And** email is delivered to recipient's inbox

- [ ] **Given** email sending fails (API error)
  - **When** NotificationSendingService processes notification
  - **Then** exception is caught
  - **And** notification is marked as FAILED
  - **And** error is logged

#### Technical Notes
- SendGrid client library
- SendGridEmailService implements EmailService
- Configuration: API key from environment variable
- Email templates for invitations

---

### User Story 6.6: Email Templates

**As a** content creator
**I want** professional-looking invitation emails
**So that** participants have a good experience

#### Acceptance Criteria

- [ ] **Given** a participant invitation
  - **When** email is sent
  - **Then** email uses HTML template
  - **And** includes project title, description, dates
  - **And** includes accept/decline links
  - **And** email is branded and professional

#### Technical Notes
- HTML email templates
- Template variables: projectTitle, participantName, acceptLink, declineLink
- Template stored in resources or database

---

## Epic: JWT Authentication

### User Story 6.7: JWT Token Generation

**As a** userJpaEntity
**I want** to authenticate and receive a JWT token
**So that** I can make authenticated API requests

#### Acceptance Criteria

- [ ] **Given** I provide valid credentials (username, password)
  - **When** I call POST /api/auth/login
  - **Then** the system validates credentials
  - **And** generates a JWT token containing userId and workspaceId
  - **And** returns the token in response

- [ ] **Given** I provide invalid credentials
  - **When** I call login endpoint
  - **Then** authentication fails
  - **And** returns 401 Unauthorized

#### Technical Notes
- JWT library (jjwt)
- Token includes: userId, workspaceId, issued at, expiration
- Secret key configuration
- Token expiration: 24 hours

---

### User Story 6.8: JWT Token Validation

**As a** system
**I want** to validate JWT tokens on every request
**So that** only authenticated users can access the API

#### Acceptance Criteria

- [ ] **Given** I send a request with valid JWT token
  - **When** API Gateway processes the request
  - **Then** token is validated
  - **And** userId and workspaceId are extracted
  - **And** SecurityContext is populated
  - **And** request proceeds to controller

- [ ] **Given** I send a request without token
  - **When** API Gateway processes the request
  - **Then** returns 401 Unauthorized

- [ ] **Given** I send a request with expired token
  - **When** API Gateway processes the request
  - **Then** returns 401 Unauthorized

- [ ] **Given** I send a request with invalid signature
  - **When** API Gateway validates token
  - **Then** returns 401 Unauthorized

#### Technical Notes
- JwtAuthenticationFilter (Spring Security)
- Validates signature, expiration
- Populates SecurityContext with userId and workspaceId

---

## Epic: REST API

### User Story 6.9: REST Controllers for All Contexts

**As a** developer
**I want** REST controllers for all bounded contexts
**So that** the API is complete and usable

#### Acceptance Criteria

- [ ] **Given** all bounded contexts
  - **When** implementing REST API
  - **Then** ProjectController has endpoints: POST /projects, GET /projects, GET /projects/{id}, POST /projects/{id}/budget-items, etc.
  - **And** WorkspaceController has: POST /workspaces, POST /workspaces/{id}/upgrade
  - **And** BrainstormController has: POST /brainstorms, POST /brainstorms/{id}/ideas, POST /brainstorms/{id}/convert
  - **And** ParticipantController has: POST /projects/{id}/participants, POST /participants/accept, POST /participants/decline

- [ ] **Given** any endpoint
  - **When** called with invalid data
  - **Then** returns 400 Bad Request with validation errors
  - **And** when called without authentication, returns 401 Unauthorized

#### Technical Notes
- Spring Boot REST controllers
- @RestController, @RequestMapping
- Request/Response DTOs
- Validation annotations (@Valid, @NotNull, etc.)

---

### User Story 6.10: CORS and Security Configuration

**As a** frontend developer
**I want** CORS configured for local development
**So that** I can call the API from a web app

#### Acceptance Criteria

- [ ] **Given** I call API from http://localhost:3000
  - **When** browser makes request
  - **Then** CORS headers allow the request
  - **And** request succeeds

---

## Epic: Docker Deployment

### User Story 6.11: Docker Compose for Local Development

**As a** developer
**I want** Docker Compose to run all infrastructure
**So that** I can start the entire system with one command

#### Acceptance Criteria

- [ ] **Given** I have Docker installed
  - **When** I run `docker-compose up`
  - **Then** PostgreSQL starts
  - **And** EventStoreDB starts
  - **And** CCPP API application starts
  - **And** all services are connected
  - **And** I can make API calls to http://localhost:8080

#### Technical Notes
- docker-compose.yml with services: postgres, eventstore, ccpp-api
- Application Dockerfile (multi-stage build)
- Environment variables for configuration
- Health checks

---

### User Story 6.12: Configuration Externalization

**As a** DevOps engineer
**I want** configuration externalized via environment variables
**So that** the same image works in different environments

#### Acceptance Criteria

- [ ] **Given** application configuration
  - **When** I deploy to different environments
  - **Then** I can configure via environment variables:
    - DATABASE_URL
    - EVENTSTORE_HOST
    - SENDGRID_API_KEY
    - JWT_SECRET
  - **And** no code changes needed

---

## Phase 6 Completion Criteria

### Functional
- [ ] All userJpaEntity stories implemented (6.1 - 6.12)
- [ ] All acceptance criteria met
- [ ] PostgreSQL integrated for read models
- [ ] EventStoreDB integrated for event sourcing
- [ ] Real emails sent via SendGrid
- [ ] JWT authentication working
- [ ] Complete REST API available
- [ ] Docker Compose deployment working

### Technical
- [ ] All in-memory implementations replaced
- [ ] Event sourcing with EventStoreDB
- [ ] Read models in PostgreSQL
- [ ] JWT authentication and authorization
- [ ] REST controllers for all contexts
- [ ] Docker containerization

### Quality
- [ ] Integration tests with real infrastructure (Testcontainers)
- [ ] Manual end-to-end test successful
- [ ] All tests pass: `mvn verify`
- [ ] Performance acceptable
- [ ] Code reviewed
- [ ] Git commit: "Phase 6 complete: Production infrastructure"

---

## Story Points Estimate

| User Story | Complexity | Points |
|------------|-----------|--------|
| 6.1 PostgreSQL Read Models | Medium | 5 |
| 6.2 All Read Models | Large | 8 |
| 6.3 EventStoreDB Integration | Large | 8 |
| 6.4 Event Replay | Medium | 5 |
| 6.5 SendGrid Integration | Medium | 5 |
| 6.6 Email Templates | Small | 3 |
| 6.7 JWT Generation | Medium | 5 |
| 6.8 JWT Validation | Medium | 5 |
| 6.9 REST Controllers | Large | 8 |
| 6.10 CORS & Security | Small | 2 |
| 6.11 Docker Compose | Medium | 5 |
| 6.12 Configuration | Small | 3 |
| **Total** | | **62** |

**Estimated Duration:** 1-2 weeks (infrastructure work can be time-consuming)
