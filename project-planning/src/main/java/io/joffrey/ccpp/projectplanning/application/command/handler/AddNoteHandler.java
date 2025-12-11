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
        var streamId = command.projectId().value().toString();
        var events = eventStore.readStream(streamId);
        var project = Project.loadFromHistory(events);

        project.addNote(command.content(), command.userId());

        var newEvents = project.uncommittedEvents();
        eventStore.append(streamId, newEvents, -1);
        project.markEventsAsCommitted();
    }
}
