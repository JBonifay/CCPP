package io.joffrey.ccpp.projectplanning.application.command.handler;

import com.ccpp.shared.domain.EventStore;
import io.joffrey.ccpp.projectplanning.application.command.MarkProjectAsReadyCommand;
import io.joffrey.ccpp.projectplanning.domain.Project;

public class MarkProjectAsReadyHandler implements CommandHandler<MarkProjectAsReadyCommand> {

    private final EventStore eventStore;

    public MarkProjectAsReadyHandler(EventStore eventStore) {
        this.eventStore = eventStore;
    }

    @Override
    public void handle(MarkProjectAsReadyCommand command) {

    }
}
