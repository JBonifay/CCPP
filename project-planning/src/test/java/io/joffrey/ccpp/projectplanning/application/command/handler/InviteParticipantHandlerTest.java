package io.joffrey.ccpp.projectplanning.application.command.handler;

import com.ccpp.shared.identities.ProjectId;
import com.ccpp.shared.identities.UserId;
import com.ccpp.shared.identities.WorkspaceId;
import com.ccpp.shared.valueobjects.DateRange;
import io.joffrey.ccpp.projectplanning.application.command.command.InviteParticipantCommand;
import io.joffrey.ccpp.projectplanning.domain.event.ParticipantInvited;
import io.joffrey.ccpp.projectplanning.domain.event.ProjectCreated;
import io.joffrey.ccpp.projectplanning.domain.exception.InvalidParticipantDataException;
import io.joffrey.ccpp.projectplanning.domain.valueobject.ParticipantId;
import com.ccpp.shared.repository.InMemoryEventStore;
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


    @Test
    void should_invite_participant_to_project() {
        eventStore.append(projectId.value(), List.of(new ProjectCreated(projectId, workspaceId, userId, title, description, timeline, projectBudgetLimit)), -1);

        handler.handle(new InviteParticipantCommand(projectId, participantId, "mcfly@example.com", "McFly"));

        assertThat(eventStore.readStream(projectId.value()))
                .last()
                .isEqualTo(new ParticipantInvited(projectId, participantId, "mcfly@example.com", "McFly"));
    }

    @Test
    void should_reject_empty_participant_email() {
        eventStore.append(projectId.value(), List.of(new ProjectCreated(projectId, workspaceId, userId, title, description, timeline, projectBudgetLimit)), -1);

        assertThatThrownBy(() -> handler.handle(new InviteParticipantCommand(projectId, new ParticipantId(UUID.randomUUID()), "", "McFly")))
                .isInstanceOf(InvalidParticipantDataException.class)
                .hasMessageContaining("Participant email cannot be empty");
    }

    @Test
    void should_reject_empty_participant_name() {
        eventStore.append(projectId.value(), List.of(new ProjectCreated(projectId, workspaceId, userId, title, description, timeline, projectBudgetLimit)), -1);

        assertThatThrownBy(() -> handler.handle(new InviteParticipantCommand(projectId, new ParticipantId(UUID.randomUUID()), "mcfly@example.com", "")))
                .isInstanceOf(InvalidParticipantDataException.class)
                .hasMessageContaining("Participant name cannot be empty");
    }

    @Test
    void should_reject_null_participant_email() {
        eventStore.append(projectId.value(), List.of(new ProjectCreated(projectId, workspaceId, userId, title, description, timeline, projectBudgetLimit)), -1);

        assertThatThrownBy(() -> handler.handle(new InviteParticipantCommand(projectId, new ParticipantId(UUID.randomUUID()), null, "McFly")))
                .isInstanceOf(InvalidParticipantDataException.class)
                .hasMessageContaining("Participant email cannot be empty");
    }

    @Test
    void should_reject_null_participant_name() {
        eventStore.append(projectId.value(), List.of(new ProjectCreated(projectId, workspaceId, userId, title, description, timeline, projectBudgetLimit)), -1);

        assertThatThrownBy(() -> handler.handle(new InviteParticipantCommand(projectId, new ParticipantId(UUID.randomUUID()), "mcfly@example.com", null)))
                .isInstanceOf(InvalidParticipantDataException.class)
                .hasMessageContaining("Participant name cannot be empty");
    }
}
