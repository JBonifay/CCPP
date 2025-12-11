package io.joffrey.ccpp.projectplanning.application.command.handler;

import com.ccpp.shared.domain.EventStore;
import io.joffrey.ccpp.projectplanning.application.command.DeclineParticipantInvitationCommand;
import io.joffrey.ccpp.projectplanning.domain.Project;

public class DeclineParticipantInvitationHandler implements CommandHandler<DeclineParticipantInvitationCommand> {

    private final EventStore eventStore;

    public DeclineParticipantInvitationHandler(EventStore eventStore) {
        this.eventStore = eventStore;
    }

    @Override
    public void handle(DeclineParticipantInvitationCommand command) {
        var streamId = command.projectId().value();
        var events = eventStore.readStream(streamId);
        var project = Project.loadFromHistory(events);

        project.participantDeclinedInvitation(command.participantId());

        var newEvents = project.uncommittedEvents();
        int expectedVersion = events.size() - 1;
        eventStore.append(streamId, newEvents, expectedVersion);
        project.markEventsAsCommitted();
    }
}
