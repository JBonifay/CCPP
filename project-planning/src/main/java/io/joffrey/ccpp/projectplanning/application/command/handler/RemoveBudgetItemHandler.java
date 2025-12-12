package io.joffrey.ccpp.projectplanning.application.command.handler;

import com.ccpp.shared.domain.DomainEvent;
import com.ccpp.shared.domain.EventStore;
import io.joffrey.ccpp.projectplanning.application.command.RemoveBudgetItemCommand;
import io.joffrey.ccpp.projectplanning.domain.Project;

import java.util.List;

public class RemoveBudgetItemHandler implements CommandHandler<RemoveBudgetItemCommand> {

    private final EventStore eventStore;

    public RemoveBudgetItemHandler(EventStore eventStore) {
        this.eventStore = eventStore;
    }

    @Override
    public void handle(RemoveBudgetItemCommand command) {
        List<DomainEvent> projectEvents = eventStore.readStream(command.projectId().value());
        Project project = Project.fromHistory(projectEvents);

        project.removeBudgetItem(command.budgetItemId());

        eventStore.append(command.projectId().value(), project.uncommittedEvents(), project.version());
        project.markEventsAsCommitted();
    }
}
