package io.joffrey.ccpp.projectplanning.application.command.handler;

import com.ccpp.shared.domain.EventStore;
import io.joffrey.ccpp.projectplanning.application.command.UpdateBudgetItemCommand;
import io.joffrey.ccpp.projectplanning.domain.Project;

public class UpdateBudgetItemHandler implements CommandHandler<UpdateBudgetItemCommand> {

    private final EventStore eventStore;

    public UpdateBudgetItemHandler(EventStore eventStore) {
        this.eventStore = eventStore;
    }

    @Override
    public void handle(UpdateBudgetItemCommand command) {
        var streamId = command.projectId().value().toString();
        var events = eventStore.readStream(streamId);
        var project = Project.loadFromHistory(events);

        project.updateBudgetItem(command.budgetItemId(), command.description(), command.newAmount());

        var newEvents = project.uncommittedEvents();
        eventStore.append(streamId, newEvents, -1);
        project.markEventsAsCommitted();
    }
}
