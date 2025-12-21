# Phase 4: Participant Saga - User Stories

## Overview
Phase 4 implements participant management, email invitation workflow, and the Notification bounded context using saga choreography.

---

## Epic: Participant Management

### User Story 4.1: Add Participants to Project

**As a** content creator
**I want to** add external participants (guests/collaborators) to my project
**So that** I can invite them to be part of the video production

#### Acceptance Criteria

- [ ] **Given** I have a project in PLANNING status
  - **When** I add a participant with email "alice@example.com" and name "Alice"
  - **Then** the participant is added to the project
  - **And** the participant has status "INVITED"
  - **And** a unique ParticipantId is generated
  - **And** a ParticipantInvited event is emitted

- [ ] **Given** I have a project with a participant "alice@example.com"
  - **When** I attempt to add the same email again
  - **Then** the system rejects the operation
  - **And** returns "DuplicateParticipantException"

- [ ] **Given** I add a participant with invalid email format
  - **When** the system validates the email
  - **Then** the system rejects the operation
  - **And** returns a validation error

#### Technical Notes
- Participant is an entity within Project aggregate
- Email uniqueness enforced per project
- Invitation status: INVITED, ACCEPTED, DECLINED

---

### User Story 4.2: Participant Response to Invitation

**As an** external participant
**I want to** accept or decline project invitations
**So that** the team knows my availability

#### Acceptance Criteria

- [ ] **Given** I am an invited participant
  - **When** I accept the invitation
  - **Then** my participant status changes to "ACCEPTED"
  - **And** responseAt timestamp is recorded
  - **And** a ParticipantAccepted event is emitted

- [ ] **Given** I am an invited participant
  - **When** I decline the invitation
  - **Then** my participant status changes to "DECLINED"
  - **And** responseAt timestamp is recorded
  - **And** a ParticipantDeclined event is emitted

- [ ] **Given** I have already accepted an invitation
  - **When** I attempt to respond again
  - **Then** the operation is idempotent (no error, no duplicate event)

#### Technical Notes
- recordParticipantResponse() method on Project
- Events include participant email for notifications

---

### User Story 4.3: Project with Participant List

**As a** content creator
**I want to** view all participants and their invitation status
**So that** I know who has confirmed participation

#### Acceptance Criteria

- [ ] **Given** I have a project with 3 participants (1 accepted, 1 declined, 1 pending)
  - **When** I query for project details
  - **Then** the ProjectDetailDTO includes all 3 participants
  - **And** each participant shows: name, email, status, responseAt
  - **And** I can see who accepted, who declined, and who hasn't responded

#### Technical Notes
- ProjectDetailProjection includes participants
- Participant data embedded in DTO

---

## Epic: Notification Context

### User Story 4.4: Create Notification Aggregate

**As a** system
**I want** notifications modeled as aggregates
**So that** email/SMS sending is managed as domain concept

#### Acceptance Criteria

- [ ] **Given** I need to send a notification
  - **When** I create a Notification aggregate
  - **Then** it has: notificationId, recipients, type (EMAIL/SMS), templateId, payload, status
  - **And** initial status is "PENDING"
  - **And** a NotificationScheduled event is emitted

- [ ] **Given** a notification is sent successfully
  - **When** I mark it as sent
  - **Then** status changes to "SENT"
  - **And** sentAt timestamp is recorded
  - **And** a NotificationSent event is emitted

- [ ] **Given** a notification fails to send
  - **When** I mark it as failed
  - **Then** status changes to "FAILED"
  - **And** error message is recorded
  - **And** a NotificationFailed event is emitted

#### Technical Notes
- Notification is separate bounded context
- NotificationType: EMAIL, SMS
- Status: PENDING, SENT, FAILED

---

### User Story 4.5: Email Service (Infrastructure)

**As a** system
**I want** an email service abstraction
**So that** emails can be sent through different providers

#### Acceptance Criteria

- [ ] **Given** I have an EmailService interface
  - **When** I implement InMemoryEmailService
  - **Then** it logs emails to console instead of sending
  - **And** displays: recipient, subject, body

- [ ] **Given** I need to send an email
  - **When** I call emailService.sendEmail(to, subject, body)
  - **Then** the email is processed
  - **And** no exceptions are thrown (for in-memory impl)

#### Technical Notes
- EmailService is a port (interface)
- InMemoryEmailService is adapter for testing
- Real implementation (SendGrid) in Phase 6

---

## Epic: Participant Invitation Saga

### User Story 4.6: Send Invitations When Project Marked Ready

**As a** content creator
**I want** invitation emails automatically sent when I mark project as READY
**So that** participants are notified without manual work

#### Acceptance Criteria

- [ ] **Given** I have a project with 2 participants in PLANNING status
  - **When** I mark the project as READY
  - **Then** a ProjectMarkedAsReady event is emitted with participant list
  - **And** ParticipantInvitationSaga listens to the event
  - **And** for each participant, a Notification aggregate is created
  - **And** 2 NotificationScheduled events are emitted

- [ ] **Given** I mark a project with 0 participants as READY
  - **When** the saga processes the event
  - **Then** no notifications are created (graceful handling)

#### Technical Notes
- Saga choreography pattern
- ParticipantInvitationSaga implements EventHandler
- Subscribes to ProjectMarkedAsReady
- Creates one notification per participant

---

### User Story 4.7: Send Notification Emails

**As a** system
**I want** notifications to trigger actual email sending
**So that** participants receive invitations

#### Acceptance Criteria

- [ ] **Given** a NotificationScheduled event is published
  - **When** NotificationSendingService handles the event
  - **Then** it loads the Notification aggregate
  - **And** calls EmailService to send the email
  - **And** marks notification as SENT
  - **And** publishes NotificationSent event

- [ ] **Given** email sending fails with exception
  - **When** NotificationSendingService handles the event
  - **Then** it catches the exception
  - **And** marks notification as FAILED
  - **And** publishes NotificationFailed event

#### Technical Notes
- NotificationSendingService implements EventHandler
- Subscribes to NotificationScheduled
- Uses EmailService infrastructure
- Error handling for resilience

---

### User Story 4.8: Complete Invitation Saga Flow

**As a** system
**I want** the full invitation saga to work end-to-end
**So that** marking a project as ready automatically sends invitations

#### Acceptance Criteria

- [ ] **Given** I execute MarkProjectAsReadyCommand for a project with 2 participants
  - **When** the saga completes
  - **Then** ProjectMarkedAsReady event was published
  - **And** 2 Notification aggregates were created
  - **And** 2 emails were "sent" (logged to console)
  - **And** 2 NotificationSent events were published
  - **And** when I query notifications, I see 2 with status "SENT"

#### Technical Notes
- Integration test covering full saga
- May need await() for async event processing
- Verify event chain: ProjectMarkedAsReady → NotificationScheduled → NotificationSent

---

## Epic: Participant Response Commands

### User Story 4.9: Accept Participation Command

**As an** external participant
**I want to** accept an invitation via API call
**So that** my acceptance is recorded in the system

#### Acceptance Criteria

- [ ] **Given** I have an invitation link with projectId and email
  - **When** I execute AcceptParticipationCommand
  - **Then** the project is loaded
  - **And** participant status is updated to ACCEPTED
  - **And** project is saved
  - **And** ParticipantAccepted event is published

- [ ] **Given** I accept a participation for non-existent project
  - **When** the command is processed
  - **Then** returns "ProjectNotFoundException"

- [ ] **Given** I accept with an email not in participant list
  - **When** the command is processed
  - **Then** returns "ParticipantNotFoundException"

#### Technical Notes
- AcceptParticipationCommand(projectId, email)
- No authentication required (invitation link includes token in real system)
- Phase 4: simple email-based lookup

---

### User Story 4.10: Decline Participation Command

**As an** external participant
**I want to** decline an invitation
**So that** the team knows I cannot participate

#### Acceptance Criteria

- [ ] **Given** I have an invitation
  - **When** I execute DeclineParticipationCommand
  - **Then** participant status is updated to DECLINED
  - **And** ParticipantDeclined event is published

- [ ] **Given** the team wants to know about declines
  - **When** ParticipantDeclined event is published
  - **Then** the team could be notified (future enhancement)
  - **And** the event is logged

---

## Epic: Saga Integration and Testing

### User Story 4.11: Wire Up Saga Event Subscriptions

**As a** developer
**I want** sagas automatically subscribed to events
**So that** the invitation flow works automatically

#### Acceptance Criteria

- [ ] **Given** the application starts
  - **When** configuration is loaded
  - **Then** ParticipantInvitationSaga is subscribed to "ProjectMarkedAsReady"
  - **And** NotificationSendingService is subscribed to "NotificationScheduled"

---

### User Story 4.12: Participant Projection Updates

**As a** system
**I want** read models to reflect participant responses
**So that** queries return current participant status

#### Acceptance Criteria

- [ ] **Given** a ParticipantAccepted event is published
  - **When** ProjectDetailProjection handles the event
  - **Then** the participant's status in the read model is updated to ACCEPTED

- [ ] **Given** a ParticipantDeclined event is published
  - **When** ProjectDetailProjection handles the event
  - **Then** the participant's status is updated to DECLINED

#### Technical Notes
- Extend ProjectDetailProjection to handle participant events

---

## Phase 4 Completion Criteria

### Functional
- [ ] All userJpaEntity stories implemented (4.1 - 4.12)
- [ ] All acceptance criteria met
- [ ] Participants can be added to projects
- [ ] Invitations sent when project marked READY
- [ ] Participants can accept/decline
- [ ] Notification context working
- [ ] Emails logged (in-memory service)

### Technical
- [ ] Participant entity in Project aggregate
- [ ] Notification bounded context implemented
- [ ] ParticipantInvitationSaga working
- [ ] NotificationSendingService working
- [ ] Event chain: ProjectMarkedAsReady → NotificationScheduled → NotificationSent
- [ ] Participant response commands working
- [ ] Projections updated with participant status

### Quality
- [ ] Integration test for complete saga
- [ ] Unit tests for all aggregates and sagas
- [ ] Test coverage: 85%+
- [ ] All tests pass: `mvn verify`
- [ ] Code reviewed
- [ ] Git commit: "Phase 4 complete: Participant invitation saga"

---

## Story Points Estimate

| User Story | Complexity | Points |
|------------|-----------|--------|
| 4.1 Add Participants | Medium | 5 |
| 4.2 Participant Response | Medium | 5 |
| 4.3 View Participants | Small | 3 |
| 4.4 Notification Aggregate | Medium | 5 |
| 4.5 Email Service | Small | 3 |
| 4.6 Send Invitations Saga | Large | 8 |
| 4.7 Send Emails | Medium | 5 |
| 4.8 Complete Saga Flow | Large | 8 |
| 4.9 Accept Command | Small | 3 |
| 4.10 Decline Command | Small | 3 |
| 4.11 Wire Up Saga | Small | 2 |
| 4.12 Participant Projections | Small | 3 |
| **Total** | | **53** |

**Estimated Duration:** 1 week (assumes Phases 1-3 complete and understanding of sagas)
