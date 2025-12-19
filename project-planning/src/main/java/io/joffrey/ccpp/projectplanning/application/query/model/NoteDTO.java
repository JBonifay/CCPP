package io.joffrey.ccpp.projectplanning.application.query.model;

import com.ccpp.shared.identities.UserId;

public record NoteDTO(
        String content,
        UserId createdBy
) {
}
