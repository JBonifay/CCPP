package io.joffrey.ccpp.projectplanning.infrastructure.rest.dto;

import io.joffrey.ccpp.projectplanning.application.query.model.NoteDTO;

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
