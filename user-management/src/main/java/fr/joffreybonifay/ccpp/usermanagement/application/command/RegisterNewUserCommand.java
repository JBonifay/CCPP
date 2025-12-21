package fr.joffreybonifay.ccpp.usermanagement.application.command;

import fr.joffreybonifay.ccpp.shared.command.Command;
import fr.joffreybonifay.ccpp.shared.identities.UserId;
import fr.joffreybonifay.ccpp.shared.valueobjects.Email;

import java.util.UUID;

public record RegisterNewUserCommand(
        UserId userId,
        Email email,
        String passwordHash,
        String fullName
) implements Command {

    @Override
    public UUID commandId() {
        return null;
    }

    @Override
    public UUID aggregateId() {
        return null;
    }

    @Override
    public UUID correlationId() {
        return null;
    }

    @Override
    public UUID causationId() {
        return null;
    }

}
