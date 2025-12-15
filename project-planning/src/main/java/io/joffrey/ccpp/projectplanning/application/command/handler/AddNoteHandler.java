package io.joffrey.ccpp.projectplanning.application.command.handler;

import com.ccpp.shared.domain.DomainEvent;
import com.ccpp.shared.domain.EventStore;
import io.joffrey.ccpp.projectplanning.application.command.command.AddNoteCommand;
import io.joffrey.ccpp.projectplanning.domain.Project;

import java.util.List;

public class AddNoteHandler implements com.ccpp.shared.command.CommandHandler<AddNoteCommand> {

    private final EventStore eventStore;

    public AddNoteHandler(EventStore eventStore) {
        this.eventStore = eventStore;
    }

    @Override
    public void handle(AddNoteCommand command) {
        List<DomainEvent> projectEvents = eventStore.readStream(command.projectId().value());
        Project project = Project.fromHistory(projectEvents);

        project.addNote(command.content(), command.userId());

        eventStore.append(command.projectId().value(), project.uncommittedEvents(), project.version());
        project.markEventsAsCommitted();
    }
}
