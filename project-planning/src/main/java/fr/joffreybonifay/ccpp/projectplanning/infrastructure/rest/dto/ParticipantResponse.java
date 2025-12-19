package fr.joffreybonifay.ccpp.projectplanning.infrastructure.rest.dto;

import fr.joffreybonifay.ccpp.projectplanning.application.query.model.ParticipantDTO;

public record ParticipantResponse(
        String participantId,
        String name,
        String email,
        String status
) {

    public static ParticipantResponse from(ParticipantDTO participantDTO) {
        return new ParticipantResponse(
                participantDTO.participantId().value().toString(),
                participantDTO.name(),
                participantDTO.email(),
                participantDTO.invitationStatus().name()
        );
    }

}
