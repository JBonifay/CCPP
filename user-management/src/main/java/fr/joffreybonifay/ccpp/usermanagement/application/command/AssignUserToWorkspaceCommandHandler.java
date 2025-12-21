package fr.joffreybonifay.ccpp.usermanagement.application.command;

import fr.joffreybonifay.ccpp.shared.command.CommandHandler;
import fr.joffreybonifay.ccpp.shared.event.DomainEvent;
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
        List<DomainEvent> userEvents = eventStore.loadEvents(command.aggregateId());
        User user = User.fromHistory(userEvents);
        int initialVersion = user.version();

        user.assignToWorkspace(command.workspaceId());

        eventStore.saveEvents(
                command.aggregateId(),
                user.uncommittedEvents(),
                initialVersion,
                command.correlationId(),
                command.causationId()
        );

    }

}
