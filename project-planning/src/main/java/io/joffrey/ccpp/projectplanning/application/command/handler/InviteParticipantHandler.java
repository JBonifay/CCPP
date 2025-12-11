package io.joffrey.ccpp.projectplanning.application.command.handler;

import com.ccpp.shared.domain.EventStore;
import io.joffrey.ccpp.projectplanning.application.command.InviteParticipantCommand;
import io.joffrey.ccpp.projectplanning.domain.Project;

public class InviteParticipantHandler implements CommandHandler<InviteParticipantCommand> {

    private final EventStore eventStore;

    public InviteParticipantHandler(EventStore eventStore) {
        this.eventStore = eventStore;
    }

    @Override
    public void handle(InviteParticipantCommand command) {
        var streamId = command.projectId().value();
        var events = eventStore.readStream(streamId);
        var project = Project.loadFromHistory(events);

        project.inviteParticipant(command.participantId(), command.email(), command.name());

        var newEvents = project.uncommittedEvents();
        int expectedVersion = events.size() - 1;
        eventStore.append(streamId, newEvents, expectedVersion);
        project.markEventsAsCommitted();
    }
}
