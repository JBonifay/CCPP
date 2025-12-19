package io.joffrey.ccpp.projectplanning.application.command.handler;

import com.ccpp.shared.command.CommandHandler;
import com.ccpp.shared.eventstore.EventStore;
import io.joffrey.ccpp.projectplanning.application.command.command.AcceptParticipantInvitationCommand;

public class AcceptParticipantInvitationHandler implements CommandHandler<AcceptParticipantInvitationCommand> {

    private final EventStore eventStore;

    public AcceptParticipantInvitationHandler(EventStore eventStore) {
        this.eventStore = eventStore;
    }

    @Override
    public void handle(AcceptParticipantInvitationCommand command) {

    }
}
