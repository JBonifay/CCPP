package io.joffrey.ccpp.projectplanning.application.command.handler;

import com.ccpp.shared.eventstore.InMemoryEventStore;
import com.ccpp.shared.identities.ProjectId;
import com.ccpp.shared.identities.UserId;
import com.ccpp.shared.identities.WorkspaceId;
import com.ccpp.shared.valueobjects.DateRange;
import io.joffrey.ccpp.projectplanning.application.command.command.InviteParticipantCommand;
import io.joffrey.ccpp.projectplanning.domain.event.ParticipantInvited;
import io.joffrey.ccpp.projectplanning.domain.event.ProjectCreated;
import io.joffrey.ccpp.projectplanning.domain.exception.InvalidParticipantDataException;
import io.joffrey.ccpp.projectplanning.domain.valueobject.ParticipantId;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class InviteParticipantHandlerTest {

    InMemoryEventStore eventStore = new InMemoryEventStore();
    InviteParticipantHandler handler = new InviteParticipantHandler(eventStore);

    WorkspaceId workspaceId = new WorkspaceId(UUID.randomUUID());
    UserId userId = new UserId(UUID.randomUUID());
    ProjectId projectId = new ProjectId(UUID.randomUUID());
    DateRange timeline = new DateRange(LocalDate.of(2025, 1, 1), LocalDate.of(2025, 3, 31));
    String title = "Q1 Video Series";
    String description = "Educational content";
    BigDecimal projectBudgetLimit = BigDecimal.valueOf(1000);
    ParticipantId participantId = new ParticipantId(UUID.randomUUID());

    UUID commandId = UUID.randomUUID();
    UUID correlationId = UUID.randomUUID();

    @Test
    void should_invite_participant_to_project() {
        eventStore.saveEvents(projectId.value(), List.of(new ProjectCreated(projectId, workspaceId, userId, title, description, timeline, projectBudgetLimit)), -1, null, null);

        handler.handle(new InviteParticipantCommand(
                commandId,
                projectId,
                participantId,
                "mcfly@example.com",
                "McFly",
                correlationId
        ));

        assertThat(eventStore.loadEvents(projectId.value()))
                .last()
                .isEqualTo(new ParticipantInvited(projectId, participantId, "mcfly@example.com", "McFly"));
    }

    @Test
    void should_reject_empty_participant_email() {
        eventStore.saveEvents(projectId.value(), List.of(new ProjectCreated(projectId, workspaceId, userId, title, description, timeline, projectBudgetLimit)), -1, null, null);

        assertThatThrownBy(() -> handler.handle(
                new InviteParticipantCommand(
                        commandId,
                        projectId,
                        new ParticipantId(UUID.randomUUID()), "", "McFly",
                        correlationId
                )))
                .isInstanceOf(InvalidParticipantDataException.class)
                .hasMessageContaining("Participant email cannot be empty");
    }

    @Test
    void should_reject_empty_participant_name() {
        eventStore.saveEvents(projectId.value(), List.of(new ProjectCreated(projectId, workspaceId, userId, title, description, timeline, projectBudgetLimit)), -1, null, null);

        assertThatThrownBy(() -> handler.handle(
                new InviteParticipantCommand(
                        commandId,
                        projectId,
                        new ParticipantId(UUID.randomUUID()),
                        "mcfly@example.com",
                        "",
                        correlationId
                )))
                .isInstanceOf(InvalidParticipantDataException.class)
                .hasMessageContaining("Participant name cannot be empty");
    }

    @Test
    void should_reject_null_participant_email() {
        eventStore.saveEvents(projectId.value(), List.of(new ProjectCreated(projectId, workspaceId, userId, title, description, timeline, projectBudgetLimit)), -1, null, null);

        assertThatThrownBy(() -> handler.handle(
                new InviteParticipantCommand(
                        commandId,
                        projectId,
                        new ParticipantId(UUID.randomUUID()),
                        null,
                        "McFly",
                        correlationId
                )))
                .isInstanceOf(InvalidParticipantDataException.class)
                .hasMessageContaining("Participant email cannot be empty");
    }

    @Test
    void should_reject_null_participant_name() {
        eventStore.saveEvents(projectId.value(), List.of(new ProjectCreated(projectId, workspaceId, userId, title, description, timeline, projectBudgetLimit)), -1, null, null);

        assertThatThrownBy(() -> handler.handle(
                new InviteParticipantCommand(
                        commandId,
                        projectId,
                        new ParticipantId(UUID.randomUUID()),
                        "mcfly@example.com",
                        null,
                        correlationId
                )))
                .isInstanceOf(InvalidParticipantDataException.class)
                .hasMessageContaining("Participant name cannot be empty");
    }
}
