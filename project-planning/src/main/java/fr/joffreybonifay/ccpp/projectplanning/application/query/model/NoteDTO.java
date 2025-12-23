package fr.joffreybonifay.ccpp.projectplanning.application.query.model;

import fr.joffreybonifay.ccpp.shared.domain.identities.UserId;

public record NoteDTO(
        String content,
        UserId createdBy
) {
}
