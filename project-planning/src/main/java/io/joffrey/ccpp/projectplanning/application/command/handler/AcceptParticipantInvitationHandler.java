package io.joffrey.ccpp.projectplanning.application.command.handler;

import com.ccpp.shared.command.CommandHandler;
import com.ccpp.shared.event.DomainEvent;
import com.ccpp.shared.eventstore.EventStore;
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
        List<DomainEvent> projectEvents = eventStore.loadEvents(command.projectId().value());
        Project project = Project.fromHistory(projectEvents);
        int initialVersion = project.version();

        project.participantAcceptedInvitation(command.participantId());

        eventStore.saveEvents(project.aggregateId(), project.uncommittedEvents(), initialVersion, command.correlationId(), command.causationId());
    }

}
