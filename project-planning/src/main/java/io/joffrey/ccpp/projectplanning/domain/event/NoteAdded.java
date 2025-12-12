package io.joffrey.ccpp.projectplanning.domain.event;

import com.ccpp.shared.identities.ProjectId;
import com.ccpp.shared.identities.UserId;
import lombok.Getter;
import lombok.experimental.Accessors;

public record NoteAdded(
        ProjectId projectId,
        String content,
        UserId userId
) implements ProjectDomainEvent {
}

