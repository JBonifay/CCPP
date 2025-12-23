package fr.joffreybonifay.ccpp.projectplanning.application.command.command;

import fr.joffreybonifay.ccpp.projectplanning.domain.valueobject.ParticipantId;
import fr.joffreybonifay.ccpp.shared.command.Command;
import fr.joffreybonifay.ccpp.shared.domain.identities.ProjectId;

import java.util.UUID;

public record InviteParticipantCommand(
        UUID commandId,
        ProjectId projectId,
        ParticipantId participantId,
        String email,
        String name,
        UUID correlationId,
        UUID causationId
) implements Command {

    public InviteParticipantCommand(ProjectId projectId, ParticipantId participantId, String email, String name, UUID correlationId, UUID causationId) {
        this(UUID.randomUUID(), projectId, participantId, email, name, correlationId, causationId);
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
