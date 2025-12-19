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
        UUID correlationId,
        UUID causationId
) implements Command {

    @Override
    public UUID aggregateId() {
        return null;
    }
}
