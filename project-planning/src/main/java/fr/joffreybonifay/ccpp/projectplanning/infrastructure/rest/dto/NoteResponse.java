package fr.joffreybonifay.ccpp.projectplanning.infrastructure.rest.dto;

import fr.joffreybonifay.ccpp.projectplanning.application.query.model.NoteDTO;

public record NoteResponse(
        String userId,
        String content
) {

    public static NoteResponse from(NoteDTO noteDTO) {
        return new NoteResponse(
                noteDTO.createdBy().value().toString(),
                noteDTO.content()
        );
    }

}
