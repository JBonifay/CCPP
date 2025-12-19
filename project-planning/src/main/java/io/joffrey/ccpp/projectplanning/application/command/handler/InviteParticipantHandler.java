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
    public void handle(InviteParticipantCommand command) {
        List<DomainEvent> projectEvents = eventStore.loadEvents(command.projectId().value());
        Project project = Project.fromHistory(projectEvents);
        int initialVersion = project.version();

        project.inviteParticipant(command.participantId(), command.email(), command.name());

        eventStore.saveEvents(command.projectId().value(), project.uncommittedCHanges(), initialVersion, command.correlationId(), command.causationId());
        project.markEventsAsCommitted();
    }
}
