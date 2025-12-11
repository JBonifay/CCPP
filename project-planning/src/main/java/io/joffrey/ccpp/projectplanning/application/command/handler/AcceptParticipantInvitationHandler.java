package io.joffrey.ccpp.projectplanning.application.command.handler;

import com.ccpp.shared.domain.EventStore;
import io.joffrey.ccpp.projectplanning.application.command.AcceptParticipantInvitationCommand;
import io.joffrey.ccpp.projectplanning.domain.Project;

public class AcceptParticipantInvitationHandler implements CommandHandler<AcceptParticipantInvitationCommand> {

    private final EventStore eventStore;

    public AcceptParticipantInvitationHandler(EventStore eventStore) {
        this.eventStore = eventStore;
    }

    @Override
    public void handle(AcceptParticipantInvitationCommand command) {
        var streamId = command.projectId().value();
        var events = eventStore.readStream(streamId);
        var project = new Project(command.projectId());
        project.loadFromHistory(events);

        project.participantAcceptedInvitation(command.participantId());

        var newEvents = project.uncommittedEvents();
        int expectedVersion = events.size() - 1;
        eventStore.append(streamId, newEvents, expectedVersion);
        project.markEventsAsCommitted();
    }
}
