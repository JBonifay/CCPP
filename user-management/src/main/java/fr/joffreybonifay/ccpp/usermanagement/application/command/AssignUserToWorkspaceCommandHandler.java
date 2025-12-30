package fr.joffreybonifay.ccpp.usermanagement.application.command;

import fr.joffreybonifay.ccpp.shared.command.CommandHandler;
import fr.joffreybonifay.ccpp.shared.domain.event.DomainEvent;
import fr.joffreybonifay.ccpp.shared.eventstore.AggregateType;
import fr.joffreybonifay.ccpp.shared.eventstore.EventMetadata;
import fr.joffreybonifay.ccpp.shared.eventstore.EventStore;
import fr.joffreybonifay.ccpp.usermanagement.domain.User;

import java.util.List;

public class AssignUserToWorkspaceCommandHandler implements CommandHandler<AssignUserToWorkspaceCommand> {

    private final EventStore eventStore;

    public AssignUserToWorkspaceCommandHandler(EventStore eventStore) {
        this.eventStore = eventStore;
    }

    @Override
    public void handle(AssignUserToWorkspaceCommand command) {
        List<DomainEvent> userEvents = eventStore.loadEvents(command.userId().value());
        User user = User.fromHistory(userEvents);
        int initialVersion = user.version();

        user.assignToWorkspace(command.workspaceId(), command.workspaceName(), command.workspaceLogoUrl());

        eventStore.saveEvents(
                command.userId().value(),
                AggregateType.USER,
                user.uncommittedEvents().stream().map(domainEvent -> new EventMetadata(
                        domainEvent,
                        command.commandId(),
                        command.correlationId(),
                        command.causationId()
                )).toList(),
                initialVersion
        );


    }

}
