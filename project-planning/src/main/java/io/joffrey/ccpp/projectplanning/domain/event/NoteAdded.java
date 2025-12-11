package io.joffrey.ccpp.projectplanning.domain.event;

import com.ccpp.shared.domain.DomainEvent;
import com.ccpp.shared.identities.ProjectId;
import com.ccpp.shared.identities.UserId;

public record NoteAdded(
    ProjectId projectId,
    String content,
    UserId userId
) implements DomainEvent {
}

