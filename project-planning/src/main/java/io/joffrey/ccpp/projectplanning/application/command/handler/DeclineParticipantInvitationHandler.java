package io.joffrey.ccpp.projectplanning.application.command.handler;

import com.ccpp.shared.domain.DomainEvent;
import com.ccpp.shared.domain.EventStore;
import io.joffrey.ccpp.projectplanning.application.command.command.DeclineParticipantInvitationCommand;
import io.joffrey.ccpp.projectplanning.domain.Project;

import java.util.List;

public class DeclineParticipantInvitationHandler implements com.ccpp.shared.command.CommandHandler<DeclineParticipantInvitationCommand> {

    private final EventStore eventStore;

    public DeclineParticipantInvitationHandler(EventStore eventStore) {
        this.eventStore = eventStore;
    }

    @Override
    public void handle(DeclineParticipantInvitationCommand command) {
        List<DomainEvent> projectEvents = eventStore.readStream(command.projectId().value());
        Project project = Project.fromHistory(projectEvents);

        project.participantDeclinedInvitation(command.participantId());

        eventStore.append(command.projectId().value(), project.uncommittedEvents(), project.version());
        project.markEventsAsCommitted();
    }
}
