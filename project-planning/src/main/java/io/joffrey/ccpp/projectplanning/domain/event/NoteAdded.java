package io.joffrey.ccpp.projectplanning.domain.event;

import com.ccpp.shared.identities.ProjectId;
import com.ccpp.shared.identities.UserId;
import com.ccpp.shared.event.DomainEvent;

public record NoteAdded(
        ProjectId projectId,
        String content,
        UserId userId
) implements DomainEvent {
}

