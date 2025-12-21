package fr.joffreybonifay.ccpp.projectplanning.application.command.handler;

import fr.joffreybonifay.ccpp.shared.event.DomainEvent;
import fr.joffreybonifay.ccpp.shared.eventstore.EventStore;
import fr.joffreybonifay.ccpp.shared.command.CommandHandler;
import fr.joffreybonifay.ccpp.projectplanning.application.command.command.RemoveBudgetItemCommand;
import fr.joffreybonifay.ccpp.projectplanning.domain.Project;

import java.util.List;

public class RemoveBudgetItemHandler implements CommandHandler<RemoveBudgetItemCommand> {

    private final EventStore eventStore;

    public RemoveBudgetItemHandler(EventStore eventStore) {
        this.eventStore = eventStore;
    }

    @Override
    public void handle(RemoveBudgetItemCommand command) {
        List<DomainEvent> projectEvents = eventStore.loadEvents(command.projectId().value());
        Project project = Project.fromHistory(projectEvents);
        int initialVersion = project.version();

        project.removeBudgetItem(command.budgetItemId());

        eventStore.saveEvents(command.projectId().value(), project.uncommittedEvents(), initialVersion, command.correlationId(), command.causationId());
        project.markEventsAsCommitted();
    }
}
