package fr.joffreybonifay.ccpp.projectplanning.application.command.command;

import fr.joffreybonifay.ccpp.shared.command.Command;
import fr.joffreybonifay.ccpp.shared.identities.ProjectId;
import fr.joffreybonifay.ccpp.shared.identities.UserId;

import java.util.UUID;

public record AddNoteCommand(
        UUID commandId,
        ProjectId projectId,
        String content,
        UserId userId,
        UUID correlationId
)  implements Command {

    public AddNoteCommand( ProjectId projectId, String content, UserId userId, UUID correlationId) {
        this(UUID.randomUUID(), projectId, content, userId, correlationId);
    }

    @Override
    public UUID aggregateId() {
        return projectId.value();
    }

    @Override
    public UUID correlationId() {
        return correlationId != null ? correlationId : commandId;
    }

    @Override
    public UUID causationId() {
        return commandId;
    }

}
