package fr.joffreybonifay.ccpp.usermanagement.application.command;

import fr.joffreybonifay.ccpp.shared.command.CommandHandler;
import fr.joffreybonifay.ccpp.shared.eventstore.EventStore;
import fr.joffreybonifay.ccpp.usermanagement.domain.User;
import fr.joffreybonifay.ccpp.usermanagement.domain.service.UserUniquenessChecker;

public class RegisterNewUserCommandHandler implements CommandHandler<RegisterNewUserCommand> {

    private final EventStore eventStore;
    private final UserUniquenessChecker uniquenessChecker;

    public RegisterNewUserCommandHandler(EventStore eventStore, UserUniquenessChecker uniquenessChecker) {
        this.eventStore = eventStore;
        this.uniquenessChecker = uniquenessChecker;
    }

    @Override
    public void handle(RegisterNewUserCommand command) {
        User newUser = User.create(
                command.userId(),
                command.email(),
                command.passwordHash(),
                command.fullName(),
                uniquenessChecker
        );

        eventStore.saveEvents(
                newUser.aggregateId(),
                newUser.uncommittedEvents(),
                newUser.version(),
                command.correlationId(),
                command.causationId()
        );
    }

}
