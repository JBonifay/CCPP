package fr.joffreybonifay.ccpp.shared.command;

import java.util.UUID;

public interface Command {
    UUID commandId();
    UUID aggregateId();
    UUID correlationId();
    UUID causationId();
}
