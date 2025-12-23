package fr.joffreybonifay.ccpp.usermanagement.application.command;

import fr.joffreybonifay.ccpp.shared.command.Command;
import fr.joffreybonifay.ccpp.shared.identities.UserId;
import fr.joffreybonifay.ccpp.shared.valueobjects.Email;

import java.util.UUID;

public record RegisterNewUserCommand(
        UUID commandId,
        UserId userId,
        Email email,
        String password,
        String fullName,
        UUID correlationId,
        UUID causationId
) implements Command {

    public RegisterNewUserCommand(UserId userId, Email email, String password, String fullName, UUID correlationId, UUID causationId) {
        this(UUID.randomUUID(), userId, email, password, fullName, correlationId, causationId);
    }

    @Override
    public UUID commandId() {
        return commandId;
    }

    @Override
    public UUID correlationId() {
        return correlationId != null ? correlationId : commandId;
    }

    @Override
    public UUID causationId() {
        return causationId != null ? causationId : commandId;
    }

}
