package fr.joffreybonifay.ccpp.projectplanning.application.command.handler;

import fr.joffreybonifay.ccpp.projectplanning.application.command.command.AddBudgetItemCommand;
import fr.joffreybonifay.ccpp.projectplanning.domain.Project;
import fr.joffreybonifay.ccpp.shared.command.CommandHandler;
import fr.joffreybonifay.ccpp.shared.event.DomainEvent;
import fr.joffreybonifay.ccpp.shared.eventstore.AggregateType;
import fr.joffreybonifay.ccpp.shared.eventstore.EventMetadata;
import fr.joffreybonifay.ccpp.shared.eventstore.EventStore;

import java.util.List;

public class AddBudgetItemHandler implements CommandHandler<AddBudgetItemCommand> {

    private final EventStore eventStore;

    public AddBudgetItemHandler(EventStore eventStore) {
        this.eventStore = eventStore;
    }

    @Override
    public void handle(AddBudgetItemCommand command) {
        List<DomainEvent> projectEvents = eventStore.loadEvents(command.projectId().value());
        Project project = Project.fromHistory(projectEvents);
        int initialVersion = project.version();

        project.addBudgetItem(command.budgetItemId(), command.description(), command.amount());

        eventStore.saveEvents(
                command.projectId().value(),
                AggregateType.PROJECT_PLANNING,
                project.uncommittedEvents().stream().map(domainEvent -> new EventMetadata(
                        domainEvent,
                        command.commandId(),
                        command.correlationId(),
                        command.causationId()
                )).toList(),
                initialVersion
        );

        project.markEventsAsCommitted();
    }
}
