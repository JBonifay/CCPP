package io.joffrey.ccpp.projectplanning.application.command.handler;

import com.ccpp.shared.domain.EventStore;
import io.joffrey.ccpp.projectplanning.application.command.AddBudgetItemCommand;
import io.joffrey.ccpp.projectplanning.domain.Project;

public class AddBudgetItemHandler implements CommandHandler<AddBudgetItemCommand> {

    private final EventStore eventStore;

    public AddBudgetItemHandler(EventStore eventStore) {
        this.eventStore = eventStore;
    }

    @Override
    public void handle(AddBudgetItemCommand command) {
        var streamId = command.projectId().value();
        var events = eventStore.readStream(streamId);
        var project = Project.loadFromHistory(events);

        project.addBudgetItem(command.budgetItemId(), command.description(), command.amount());

        var newEvents = project.uncommittedEvents();
        int expectedVersion = events.size() - 1;
        eventStore.append(streamId, newEvents, expectedVersion);
        project.markEventsAsCommitted();
    }
}
