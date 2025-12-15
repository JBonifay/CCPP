package io.joffrey.ccpp.projectplanning.application.command.command;

import com.ccpp.shared.command.Command;
import com.ccpp.shared.identities.ProjectId;
import io.joffrey.ccpp.projectplanning.domain.valueobject.ParticipantId;

public record InviteParticipantCommand(
        ProjectId projectId,
        ParticipantId participantId,
        String email,
        String name
) implements Command {
}
