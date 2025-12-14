package io.joffrey.ccpp.projectplanning.application.command.command;

import com.ccpp.shared.identities.ProjectId;
import io.joffrey.ccpp.projectplanning.domain.valueobject.ParticipantId;

public record DeclineParticipantInvitationCommand(
        ProjectId projectId,
        ParticipantId participantId
) {
}
