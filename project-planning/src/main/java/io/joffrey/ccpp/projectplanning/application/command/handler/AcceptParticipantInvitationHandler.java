package io.joffrey.ccpp.projectplanning.application.command.handler;

import com.ccpp.shared.event.DomainEvent;
import com.ccpp.shared.eventstore.EventStore;
import com.ccpp.shared.command.CommandHandler;
import io.joffrey.ccpp.projectplanning.application.command.command.AcceptParticipantInvitationCommand;
import io.joffrey.ccpp.projectplanning.domain.Project;

import java.util.List;

public class AcceptParticipantInvitationHandler implements CommandHandler<AcceptParticipantInvitationCommand> {

    private final EventStore eventStore;

    public AcceptParticipantInvitationHandler(EventStore eventStore) {
        this.eventStore = eventStore;
    }

    @Override
    public void handle(AcceptParticipantInvitationCommand command) {
//        List<DomainEvent> projectEvents = eventStore.loadEvents(command.projectId().value());
//        Project project = Project.fromHistory(projectEvents);
//
//        project.participantAcceptedInvitation(command.participantId());
//        eventStore.saveEvents(command.projectId().value(), project.uncommittedEvents(), project.version());
//        project.markEventsAsCommitted();
    }
}
