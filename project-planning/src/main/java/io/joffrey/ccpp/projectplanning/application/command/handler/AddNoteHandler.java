package io.joffrey.ccpp.projectplanning.application.command.handler;

import com.ccpp.shared.domain.EventStore;
import io.joffrey.ccpp.projectplanning.application.command.AddNoteCommand;
import io.joffrey.ccpp.projectplanning.domain.Project;

public class AddNoteHandler implements CommandHandler<AddNoteCommand> {

    private final EventStore eventStore;

    public AddNoteHandler(EventStore eventStore) {
        this.eventStore = eventStore;
    }

    @Override
    public void handle(AddNoteCommand command) {

    }
}
