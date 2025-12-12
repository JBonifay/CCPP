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

    }
}
