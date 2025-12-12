package io.joffrey.ccpp.projectplanning.application.command.handler;

import com.ccpp.shared.domain.DomainEvent;
import com.ccpp.shared.domain.EventStore;
import io.joffrey.ccpp.projectplanning.application.command.UpdateBudgetItemCommand;
import io.joffrey.ccpp.projectplanning.domain.Project;

import java.util.List;

public class UpdateBudgetItemHandler implements CommandHandler<UpdateBudgetItemCommand> {

    private final EventStore eventStore;

    public UpdateBudgetItemHandler(EventStore eventStore) {
        this.eventStore = eventStore;
    }

    @Override
    public void handle(UpdateBudgetItemCommand command) {
        List<DomainEvent> projectEvents = eventStore.readStream(command.projectId().value());
        Project project = Project.fromHistory(projectEvents);

        project.updateBudgetItem(command.budgetItemId(), command.description(), command.newAmount());

        eventStore.append(command.projectId().value(), project.uncommittedEvents(), project.version());
        project.markEventsAsCommitted();
    }
}
