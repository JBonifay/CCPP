package io.joffrey.ccpp.projectplanning.application.command.command;

import com.ccpp.shared.infrastructure.command.Command;
import com.ccpp.shared.domain.identities.ProjectId;
import io.joffrey.ccpp.projectplanning.domain.valueobject.ParticipantId;

public record DeclineParticipantInvitationCommand(
        ProjectId projectId,
        ParticipantId participantId
)  implements Command {
}
