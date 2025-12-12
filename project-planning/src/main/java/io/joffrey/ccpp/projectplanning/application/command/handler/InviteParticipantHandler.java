package io.joffrey.ccpp.projectplanning.application.command.handler;

import com.ccpp.shared.domain.DomainEvent;
import com.ccpp.shared.domain.EventStore;
import io.joffrey.ccpp.projectplanning.application.command.InviteParticipantCommand;
import io.joffrey.ccpp.projectplanning.domain.Project;

import java.util.List;

public class InviteParticipantHandler implements CommandHandler<InviteParticipantCommand> {

    private final EventStore eventStore;

    public InviteParticipantHandler(EventStore eventStore) {
        this.eventStore = eventStore;
    }

    @Override
    public void handle(InviteParticipantCommand inviteParticipantCommand) {
        List<DomainEvent> projectEvents = eventStore.readStream(inviteParticipantCommand.projectId().value());
        Project project = Project.fromHistory(projectEvents);

        project.inviteParticipant(inviteParticipantCommand.participantId(), inviteParticipantCommand.email(), inviteParticipantCommand.name());

        eventStore.append(inviteParticipantCommand.projectId().value(), project.uncommittedEvents(), project.version());
        project.markEventsAsCommitted();
    }
}
