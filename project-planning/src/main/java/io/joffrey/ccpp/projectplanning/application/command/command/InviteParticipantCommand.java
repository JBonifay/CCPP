package io.joffrey.ccpp.projectplanning.application.command.command;

import com.ccpp.shared.command.Command;
import com.ccpp.shared.identities.ProjectId;
import io.joffrey.ccpp.projectplanning.domain.valueobject.ParticipantId;

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
