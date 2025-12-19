package io.joffrey.ccpp.projectplanning.application.command.command;

import com.ccpp.shared.command.Command;
import com.ccpp.shared.identities.ProjectId;
import io.joffrey.ccpp.projectplanning.domain.valueobject.ParticipantId;

import java.util.UUID;

public record InviteParticipantCommand(
        ProjectId projectId,
        ParticipantId participantId,
        String email,
        String name
) implements Command {
    @Override
    public UUID getCommandId() {
        return null;
    }

    @Override
    public UUID getAggregateId() {
        return null;
    }

    @Override
    public UUID getCorrelationId() {
        return null;
    }

    @Override
    public UUID getCausationId() {
        return null;
    }
}
