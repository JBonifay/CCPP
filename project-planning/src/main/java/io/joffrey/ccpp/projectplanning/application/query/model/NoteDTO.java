package io.joffrey.ccpp.projectplanning.application.query.model;

import com.ccpp.shared.identities.UserId;

import java.time.Instant;

public record NoteDTO(
        String content,
        UserId createdBy,
        Instant createdAt
) {
}
