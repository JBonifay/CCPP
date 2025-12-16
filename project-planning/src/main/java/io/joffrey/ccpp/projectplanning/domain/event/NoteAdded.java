package io.joffrey.ccpp.projectplanning.domain.event;

import com.ccpp.shared.domain.identities.ProjectId;
import com.ccpp.shared.domain.identities.UserId;
import com.ccpp.shared.infrastructure.event.DomainEvent;

public record NoteAdded(
        ProjectId projectId,
        String content,
        UserId userId
) implements DomainEvent {
}

