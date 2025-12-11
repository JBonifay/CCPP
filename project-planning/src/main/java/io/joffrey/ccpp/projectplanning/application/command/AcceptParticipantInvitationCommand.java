package io.joffrey.ccpp.projectplanning.application.command;

import com.ccpp.shared.identities.ProjectId;
import io.joffrey.ccpp.projectplanning.domain.valueobject.ParticipantId;

public record AcceptParticipantInvitationCommand(
        ProjectId projectId,
        ParticipantId participantId
) {
}
