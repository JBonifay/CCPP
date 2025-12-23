package fr.joffreybonifay.ccpp.projectplanning.application.command.handler;

import fr.joffreybonifay.ccpp.shared.event.ProjectCreationRequested;
import fr.joffreybonifay.ccpp.shared.eventbus.EventBus;
import fr.joffreybonifay.ccpp.shared.eventbus.SimpleEventBus;
import fr.joffreybonifay.ccpp.shared.eventstore.AggregateType;
import fr.joffreybonifay.ccpp.shared.eventstore.EventMetadata;
import fr.joffreybonifay.ccpp.shared.eventstore.impl.InMemoryEventStore;
import fr.joffreybonifay.ccpp.shared.identities.ProjectId;
import fr.joffreybonifay.ccpp.shared.identities.UserId;
import fr.joffreybonifay.ccpp.shared.identities.WorkspaceId;
import fr.joffreybonifay.ccpp.shared.valueobjects.DateRange;
import fr.joffreybonifay.ccpp.projectplanning.application.command.command.RejectInvitationCommand;
import fr.joffreybonifay.ccpp.projectplanning.domain.event.ParticipantDeclinedInvitation;
import fr.joffreybonifay.ccpp.projectplanning.domain.event.ParticipantInvited;
import fr.joffreybonifay.ccpp.projectplanning.domain.valueobject.ParticipantId;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class RejectInvitiationHandlerTest {

    EventBus eventBus = new SimpleEventBus();
    InMemoryEventStore eventStore = new InMemoryEventStore(eventBus);
    RejectInvitiationHandler handler = new RejectInvitiationHandler(eventStore);

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
    void should_decline_participant_invitation() {
        eventStore.saveEvents(projectId.value(),
                AggregateType.PROJECT_PLANNING,
                List.of(
                        new EventMetadata(new ProjectCreationRequested(projectId, workspaceId, userId, title, description, timeline, projectBudgetLimit), null, null, null),
                        new EventMetadata(new ParticipantInvited(projectId, participantId, "mcfly@example.com", "McFly"), null, null, null)),
                -1);

        handler.handle(new RejectInvitationCommand(
                commandId,
                projectId,
                participantId,
                correlationId
        ));

        assertThat(eventStore.loadEvents(projectId.value()))
                .last()
                .isEqualTo(new ParticipantDeclinedInvitation(projectId, participantId));
    }
}
