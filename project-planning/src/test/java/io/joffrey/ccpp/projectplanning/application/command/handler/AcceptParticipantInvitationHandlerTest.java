package io.joffrey.ccpp.projectplanning.application.command.handler;

import com.ccpp.shared.eventbus.EventBus;
import com.ccpp.shared.eventbus.SimpleEventBus;
import com.ccpp.shared.eventstore.InMemoryEventStore;
import com.ccpp.shared.identities.ProjectId;
import com.ccpp.shared.identities.UserId;
import com.ccpp.shared.identities.WorkspaceId;
import com.ccpp.shared.valueobjects.DateRange;
import io.joffrey.ccpp.projectplanning.application.command.command.AcceptParticipantInvitationCommand;
import io.joffrey.ccpp.projectplanning.domain.event.ParticipantAcceptedInvitation;
import io.joffrey.ccpp.projectplanning.domain.event.ParticipantInvited;
import io.joffrey.ccpp.projectplanning.domain.event.ProjectCreated;
import io.joffrey.ccpp.projectplanning.domain.valueobject.ParticipantId;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class AcceptParticipantInvitationHandlerTest {

    EventBus eventBus = new SimpleEventBus();
    InMemoryEventStore eventStore = new InMemoryEventStore(eventBus);
    AcceptParticipantInvitationHandler handler = new AcceptParticipantInvitationHandler(eventStore);

    WorkspaceId workspaceId = new WorkspaceId(UUID.randomUUID());
    UserId userId = new UserId(UUID.randomUUID());
    ProjectId projectId = new ProjectId(UUID.randomUUID());
    DateRange timeline = new DateRange(LocalDate.of(2025, 1, 1), LocalDate.of(2025, 3, 31));
    String title = "Q1 Video Series";
    String description = "Educational content";
    BigDecimal projectBudgetLimit = BigDecimal.valueOf(1000);

    UUID commandId = UUID.randomUUID();
    UUID correlationId = UUID.randomUUID();

    @Test
    void should_accept_participant_invitation() {
        ParticipantId participantId = new ParticipantId(UUID.randomUUID());
        eventStore.saveEvents(projectId.value(), List.of(
                new ProjectCreated(projectId, workspaceId, userId, title, description, timeline, projectBudgetLimit),
                new ParticipantInvited(projectId, participantId, "mcfly@example.com", "McFly")
        ), -1, correlationId, commandId);

        handler.handle(new AcceptParticipantInvitationCommand(commandId, projectId, participantId, correlationId));

        assertThat(eventStore.loadEvents(projectId.value()))
                .last()
                .isEqualTo(new ParticipantAcceptedInvitation(projectId, participantId));

    }

}
