package io.joffrey.ccpp.projectplanning.application.command.handler;

import com.ccpp.shared.domain.EventStore;
import io.joffrey.ccpp.projectplanning.application.command.ChangeProjectTimelineCommand;
import io.joffrey.ccpp.projectplanning.domain.Project;

public class ChangeProjectTimelineHandler implements CommandHandler<ChangeProjectTimelineCommand> {

    private final EventStore eventStore;

    public ChangeProjectTimelineHandler(EventStore eventStore) {
        this.eventStore = eventStore;
    }

    @Override
    public void handle(ChangeProjectTimelineCommand command) {

    }
}
