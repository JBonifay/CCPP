package io.joffrey.ccpp.projectplanning.application.command.handler;

import com.ccpp.shared.infrastructure.event.DomainEvent;
import com.ccpp.shared.infrastructure.event.EventStore;
import com.ccpp.shared.infrastructure.command.CommandHandler;
import io.joffrey.ccpp.projectplanning.application.command.command.AddBudgetItemCommand;
import io.joffrey.ccpp.projectplanning.domain.Project;

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

        project.addBudgetItem(command.budgetItemId(), command.description(), command.amount());

        eventStore.saveEvents(command.projectId().value(), project.uncommittedEvents(), project.version());
        project.markEventsAsCommitted();
    }
}
