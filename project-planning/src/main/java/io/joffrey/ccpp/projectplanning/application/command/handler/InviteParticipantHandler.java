package io.joffrey.ccpp.projectplanning.application.command.handler;

import com.ccpp.shared.event.DomainEvent;
import com.ccpp.shared.eventstore.EventStore;
import com.ccpp.shared.command.CommandHandler;
import io.joffrey.ccpp.projectplanning.application.command.command.InviteParticipantCommand;
import io.joffrey.ccpp.projectplanning.domain.Project;

import java.util.List;

public class InviteParticipantHandler implements CommandHandler<InviteParticipantCommand> {

    private final EventStore eventStore;

    public InviteParticipantHandler(EventStore eventStore) {
        this.eventStore = eventStore;
    }

    @Override
    public void handle(InviteParticipantCommand inviteParticipantCommand) {
//        List<DomainEvent> projectEvents = eventStore.loadEvents(inviteParticipantCommand.projectId().value());
//        Project project = Project.fromHistory(projectEvents);
//
//        project.inviteParticipant(inviteParticipantCommand.participantId(), inviteParticipantCommand.email(), inviteParticipantCommand.name());
//
//        eventStore.saveEvents(inviteParticipantCommand.projectId().value(), project.uncommittedEvents(), project.version());
//        project.markEventsAsCommitted();
    }
}
