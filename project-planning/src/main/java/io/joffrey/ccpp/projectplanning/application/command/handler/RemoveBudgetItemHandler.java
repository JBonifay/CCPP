package io.joffrey.ccpp.projectplanning.application.command.handler;

import com.ccpp.shared.domain.EventStore;
import io.joffrey.ccpp.projectplanning.application.command.RemoveBudgetItemCommand;
import io.joffrey.ccpp.projectplanning.domain.Project;

public class RemoveBudgetItemHandler implements CommandHandler<RemoveBudgetItemCommand> {

    private final EventStore eventStore;

    public RemoveBudgetItemHandler(EventStore eventStore) {
        this.eventStore = eventStore;
    }

    @Override
    public void handle(RemoveBudgetItemCommand command) {
        var streamId = command.projectId().value();
        var events = eventStore.readStream(streamId);
        var project = new Project(command.projectId());
        project.loadFromHistory(events);

        project.removeBudgetItem(command.budgetItemId());

        var newEvents = project.uncommittedEvents();
        int expectedVersion = events.size() - 1;
        eventStore.append(streamId, newEvents, expectedVersion);
        project.clearUncommittedEvents();
    }
}
