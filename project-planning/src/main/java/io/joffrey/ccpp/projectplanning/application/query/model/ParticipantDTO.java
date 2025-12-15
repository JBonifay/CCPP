package io.joffrey.ccpp.projectplanning.application.query.model;

import io.joffrey.ccpp.projectplanning.domain.model.InvitationStatus;
import io.joffrey.ccpp.projectplanning.domain.valueobject.ParticipantId;

public record ParticipantDTO(
        ParticipantId participantId,
        String name,
        String email,
        InvitationStatus invitationStatus
) {
}
