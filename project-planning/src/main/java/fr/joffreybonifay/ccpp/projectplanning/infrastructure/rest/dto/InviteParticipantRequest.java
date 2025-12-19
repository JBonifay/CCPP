package fr.joffreybonifay.ccpp.projectplanning.infrastructure.rest.dto;

public record InviteParticipantRequest(
        String email,
        String name
) {
}
