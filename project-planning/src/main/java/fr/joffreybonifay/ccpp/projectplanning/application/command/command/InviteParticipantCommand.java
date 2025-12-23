package fr.joffreybonifay.ccpp.projectplanning.application.command.command;

import fr.joffreybonifay.ccpp.shared.command.Command;
import fr.joffreybonifay.ccpp.shared.domain.identities.ProjectId;
import fr.joffreybonifay.ccpp.projectplanning.domain.valueobject.ParticipantId;

import java.util.UUID;

public record InviteParticipantCommand(
        UUID commandId,
        ProjectId projectId,
        ParticipantId participantId,
        String email,
        String name,
        UUID correlationId
) implements Command {

    public InviteParticipantCommand( ProjectId projectId, ParticipantId participantId, String email, String name, UUID correlationId) {
        this(UUID.randomUUID(), projectId, participantId, email, name, correlationId);
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
