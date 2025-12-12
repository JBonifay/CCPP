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

    }
}
