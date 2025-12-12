//package io.joffrey.ccpp.projectplanning.application.command.handler;
//
//import com.ccpp.shared.identities.ProjectId;
//import com.ccpp.shared.identities.UserId;
//import com.ccpp.shared.identities.WorkspaceId;
//import com.ccpp.shared.valueobjects.DateRange;
//import io.joffrey.ccpp.projectplanning.application.command.AcceptParticipantInvitationCommand;
//import io.joffrey.ccpp.projectplanning.domain.event.ParticipantAcceptedInvitation;
//import io.joffrey.ccpp.projectplanning.domain.event.ParticipantInvited;
//import io.joffrey.ccpp.projectplanning.domain.event.ProjectCreated;
//import io.joffrey.ccpp.projectplanning.domain.valueobject.ParticipantId;
//import io.joffrey.ccpp.projectplanning.infrastructure.event.InMemoryEventStore;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//
//import java.math.BigDecimal;
//import java.time.LocalDate;
//import java.util.List;
//import java.util.UUID;
//
//import static org.assertj.core.api.Assertions.assertThat;
//
//class AcceptParticipantInvitationHandlerTest {
//
//    private InMemoryEventStore eventStore;
//    private AcceptParticipantInvitationHandler handler;
//
//    private WorkspaceId workspaceId;
//    private UserId userId;
//    private ProjectId projectId;
//    private DateRange timeline;
//    private String title;
//    private String description;
//    private BigDecimal projectBudgetLimit;
//
//    @BeforeEach
//    void setUp() {
//        eventStore = new InMemoryEventStore();
//        handler = new AcceptParticipantInvitationHandler(eventStore);
//
//        workspaceId = new WorkspaceId(UUID.randomUUID());
//        userId = new UserId(UUID.randomUUID());
//        projectId = new ProjectId(UUID.randomUUID());
//        timeline = new DateRange(LocalDate.of(2025, 1, 1), LocalDate.of(2025, 3, 31));
//        title = "Q1 Video Series";
//        description = "Educational content";
//        projectBudgetLimit = BigDecimal.valueOf(1000);
//    }
//
//    @Test
//    void should_accept_participant_invitation() {
//        var participantId = new ParticipantId(UUID.randomUUID());
//        var projectCreatedEvent = new ProjectCreated(
//                projectId,
//                workspaceId,
//                userId,
//                title,
//                description,
//                timeline,
//                projectBudgetLimit,
//                0
//        );
//        var participantInvitedEvent = new ParticipantInvited(
//                projectId,
//                participantId,
//                "mcfly@example.com",
//                "McFly",
//                1
//        );
//        eventStore.append(projectId.value(), List.of(projectCreatedEvent, participantInvitedEvent), -1);
//
//        handler.handle(new AcceptParticipantInvitationCommand(projectId, participantId));
//
//        assertThat(eventStore.readStream(projectId.value()))
//                .contains(projectCreatedEvent, participantInvitedEvent)
//                .satisfies(events -> {
//                    assertThat(events.get(2)).isInstanceOf(ParticipantAcceptedInvitation.class);
//                    var acceptedEvent = (ParticipantAcceptedInvitation) events.get(2);
//                    assertThat(acceptedEvent.projectId()).isEqualTo(projectId);
//                    assertThat(acceptedEvent.participantId()).isEqualTo(participantId);
//                });
//    }
//}
