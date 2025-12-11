package io.joffrey.ccpp.projectplanning.application.command.handler;

import com.ccpp.shared.identities.ProjectId;
import com.ccpp.shared.identities.UserId;
import com.ccpp.shared.identities.WorkspaceId;
import com.ccpp.shared.valueobjects.DateRange;
import io.joffrey.ccpp.projectplanning.application.command.InviteParticipantCommand;
import io.joffrey.ccpp.projectplanning.domain.event.ParticipantInvited;
import io.joffrey.ccpp.projectplanning.domain.event.ProjectCreated;
import io.joffrey.ccpp.projectplanning.domain.exception.InvalidParticipantDataException;
import io.joffrey.ccpp.projectplanning.domain.valueobject.ParticipantId;
import io.joffrey.ccpp.projectplanning.infrastructure.event.InMemoryEventStore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class InviteParticipantHandlerTest {

    private InMemoryEventStore eventStore;
    private InviteParticipantHandler handler;

    private WorkspaceId workspaceId;
    private UserId userId;
    private ProjectId projectId;
    private DateRange timeline;
    private String title;
    private String description;
    private BigDecimal projectBudgetLimit;

    @BeforeEach
    void setUp() {
        eventStore = new InMemoryEventStore();
        handler = new InviteParticipantHandler(eventStore);

        workspaceId = new WorkspaceId(UUID.randomUUID());
        userId = new UserId(UUID.randomUUID());
        projectId = new ProjectId(UUID.randomUUID());
        timeline = new DateRange(LocalDate.of(2025, 1, 1), LocalDate.of(2025, 3, 31));
        title = "Q1 Video Series";
        description = "Educational content";
        projectBudgetLimit = BigDecimal.valueOf(1000);
    }

    @Test
    void should_invite_participant_to_project() {
        // GIVEN - project exists
        var projectCreatedEvent = new ProjectCreated(
                workspaceId,
                userId,
                projectId,
                title,
                description,
                timeline,
                projectBudgetLimit
        );
        eventStore.append(projectId.value(), java.util.List.of(projectCreatedEvent), -1);

        var participantId = new ParticipantId(UUID.randomUUID());
        var command = new InviteParticipantCommand(projectId, participantId, "mcfly@example.com", "McFly");

        // WHEN
        handler.handle(command);

        // THEN
        var events = eventStore.readStream(projectId.value());
        assertThat(events).hasSize(2);
        assertThat(events.get(1)).isInstanceOf(ParticipantInvited.class);

        var participantInvitedEvent = (ParticipantInvited) events.get(1);
        assertThat(participantInvitedEvent.projectId()).isEqualTo(projectId);
        assertThat(participantInvitedEvent.participantId()).isEqualTo(participantId);
        assertThat(participantInvitedEvent.mail()).isEqualTo("mcfly@example.com");
        assertThat(participantInvitedEvent.name()).isEqualTo("McFly");
    }

    @Test
    void should_reject_empty_participant_email() {
        // GIVEN - project exists
        var projectCreatedEvent = new ProjectCreated(
                workspaceId,
                userId,
                projectId,
                title,
                description,
                timeline,
                projectBudgetLimit
        );
        eventStore.append(projectId.value(), java.util.List.of(projectCreatedEvent), -1);

        var participantId = new ParticipantId(UUID.randomUUID());
        var command = new InviteParticipantCommand(projectId, participantId, "", "McFly");  // empty email

        // WHEN & THEN
        assertThatThrownBy(() -> handler.handle(command))
                .isInstanceOf(InvalidParticipantDataException.class)
                .hasMessageContaining("Participant email cannot be empty");

        var events = eventStore.readStream(projectId.value());
        assertThat(events).hasSize(1);
    }

    @Test
    void should_reject_empty_participant_name() {
        // GIVEN - project exists
        var projectCreatedEvent = new ProjectCreated(
                workspaceId,
                userId,
                projectId,
                title,
                description,
                timeline,
                projectBudgetLimit
        );
        eventStore.append(projectId.value(), java.util.List.of(projectCreatedEvent), -1);

        var participantId = new ParticipantId(UUID.randomUUID());
        var command = new InviteParticipantCommand(projectId, participantId, "mcfly@example.com", "");  // empty name

        // WHEN & THEN
        assertThatThrownBy(() -> handler.handle(command))
                .isInstanceOf(InvalidParticipantDataException.class)
                .hasMessageContaining("Participant name cannot be empty");

        var events = eventStore.readStream(projectId.value());
        assertThat(events).hasSize(1);
    }

    @Test
    void should_reject_null_participant_email() {
        // GIVEN - project exists
        var projectCreatedEvent = new ProjectCreated(
                workspaceId,
                userId,
                projectId,
                title,
                description,
                timeline,
                projectBudgetLimit
        );
        eventStore.append(projectId.value(), java.util.List.of(projectCreatedEvent), -1);

        var participantId = new ParticipantId(UUID.randomUUID());
        var command = new InviteParticipantCommand(projectId, participantId, null, "McFly");  // null email

        // WHEN & THEN
        assertThatThrownBy(() -> handler.handle(command))
                .isInstanceOf(InvalidParticipantDataException.class)
                .hasMessageContaining("Participant email cannot be empty");

        var events = eventStore.readStream(projectId.value());
        assertThat(events).hasSize(1);
    }

    @Test
    void should_reject_null_participant_name() {
        // GIVEN - project exists
        var projectCreatedEvent = new ProjectCreated(
                workspaceId,
                userId,
                projectId,
                title,
                description,
                timeline,
                projectBudgetLimit
        );
        eventStore.append(projectId.value(), java.util.List.of(projectCreatedEvent), -1);

        var participantId = new ParticipantId(UUID.randomUUID());
        var command = new InviteParticipantCommand(projectId, participantId, "mcfly@example.com", null);  // null name

        // WHEN & THEN
        assertThatThrownBy(() -> handler.handle(command))
                .isInstanceOf(InvalidParticipantDataException.class)
                .hasMessageContaining("Participant name cannot be empty");

        var events = eventStore.readStream(projectId.value());
        assertThat(events).hasSize(1);
    }
}
