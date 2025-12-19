package fr.joffreybonifay.ccpp.projectplanning.application.query.model;

import fr.joffreybonifay.ccpp.projectplanning.domain.model.InvitationStatus;
import fr.joffreybonifay.ccpp.projectplanning.domain.valueobject.ParticipantId;

public record ParticipantDTO(
        ParticipantId participantId,
        String name,
        String email,
        InvitationStatus invitationStatus
) {
}
