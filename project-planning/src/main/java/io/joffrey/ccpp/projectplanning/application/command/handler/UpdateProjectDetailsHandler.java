package io.joffrey.ccpp.projectplanning.application.command.handler;

import com.ccpp.shared.domain.EventStore;
import io.joffrey.ccpp.projectplanning.application.command.UpdateProjectDetailsCommand;
import io.joffrey.ccpp.projectplanning.domain.Project;

public class UpdateProjectDetailsHandler implements CommandHandler<UpdateProjectDetailsCommand> {

    private final EventStore eventStore;

    public UpdateProjectDetailsHandler(EventStore eventStore) {
        this.eventStore = eventStore;
    }

    @Override
    public void handle(UpdateProjectDetailsCommand command) {

    }
}
