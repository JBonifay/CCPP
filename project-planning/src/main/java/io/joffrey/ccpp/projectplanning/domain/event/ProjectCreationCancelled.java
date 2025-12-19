package io.joffrey.ccpp.projectplanning.domain.event;

import com.ccpp.shared.event.DomainEvent;
import com.ccpp.shared.identities.ProjectId;

public record ProjectCreationCancelled(
        ProjectId projectId,
        String reason
) implements DomainEvent {
}
