package fr.joffreybonifay.ccpp.usermanagement.application.command;

import fr.joffreybonifay.ccpp.shared.command.CommandHandler;
import fr.joffreybonifay.ccpp.shared.eventstore.AggregateType;
import fr.joffreybonifay.ccpp.shared.eventstore.EventMetadata;
import fr.joffreybonifay.ccpp.shared.eventstore.EventStore;
import fr.joffreybonifay.ccpp.usermanagement.domain.User;
import fr.joffreybonifay.ccpp.usermanagement.domain.service.UserUniquenessChecker;
import org.springframework.security.crypto.password.PasswordEncoder;

public class RegisterNewUserCommandHandler implements CommandHandler<RegisterNewUserCommand> {

    private final EventStore eventStore;
    private final PasswordEncoder passwordEncoder;
    private final UserUniquenessChecker uniquenessChecker;

    public RegisterNewUserCommandHandler(EventStore eventStore, PasswordEncoder passwordEncoder, UserUniquenessChecker uniquenessChecker) {
        this.eventStore = eventStore;
        this.passwordEncoder = passwordEncoder;
        this.uniquenessChecker = uniquenessChecker;
    }

    @Override
    public void handle(RegisterNewUserCommand command) {
        User newUser = User.create(
                command.userId(),
                command.email(),
                passwordEncoder.encode(command.password()),
                command.fullName(),
                uniquenessChecker
        );

        eventStore.saveEvents(
                command.userId().value(),
                AggregateType.USER,
                newUser.uncommittedEvents().stream().map(domainEvent -> new EventMetadata(
                        domainEvent,
                        command.commandId(),
                        command.correlationId(),
                        command.causationId()
                )).toList(),
                newUser.version()
        );

        newUser.markEventsAsCommitted();
    }

}
