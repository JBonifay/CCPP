package io.joffrey.ccpp.projectplanning.application.command.handler;

import com.ccpp.shared.identities.ProjectId;
import com.ccpp.shared.identities.UserId;
import com.ccpp.shared.identities.WorkspaceId;
import com.ccpp.shared.valueobjects.DateRange;
import io.joffrey.ccpp.projectplanning.application.command.command.AcceptParticipantInvitationCommand;
import io.joffrey.ccpp.projectplanning.domain.event.ParticipantAcceptedInvitation;
import io.joffrey.ccpp.projectplanning.domain.event.ParticipantInvited;
import io.joffrey.ccpp.projectplanning.domain.event.ProjectCreated;
import io.joffrey.ccpp.projectplanning.domain.valueobject.ParticipantId;
import com.ccpp.shared.repository.InMemoryEventStore;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class AcceptParticipantInvitationHandlerTest {

    InMemoryEventStore eventStore = new InMemoryEventStore();
    AcceptParticipantInvitationHandler handler = new AcceptParticipantInvitationHandler(eventStore);

    WorkspaceId workspaceId = new WorkspaceId(UUID.randomUUID());
    UserId userId = new UserId(UUID.randomUUID());
    ProjectId projectId = new ProjectId(UUID.randomUUID());
    DateRange timeline = new DateRange(LocalDate.of(2025, 1, 1), LocalDate.of(2025, 3, 31));
    String title = "Q1 Video Series";
    String description = "Educational content";
    BigDecimal projectBudgetLimit = BigDecimal.valueOf(1000);


    @Test
    void should_accept_participant_invitation() {
        var participantId = new ParticipantId(UUID.randomUUID());
        var projectCreatedEvent = new ProjectCreated(projectId, workspaceId, userId, title, description, timeline, projectBudgetLimit);
        var participantInvitedEvent = new ParticipantInvited(projectId, participantId, "mcfly@example.com", "McFly");
        eventStore.append(projectId.value(), List.of(projectCreatedEvent, participantInvitedEvent), -1);

        handler.handle(new AcceptParticipantInvitationCommand(projectId, participantId));

        assertThat(eventStore.readStream(projectId.value()))
                .last()
                .isEqualTo(
                        new ParticipantAcceptedInvitation(projectId, participantId)
                );

    }
}
