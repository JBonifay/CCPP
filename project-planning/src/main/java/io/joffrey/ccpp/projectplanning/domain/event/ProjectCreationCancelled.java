package io.joffrey.ccpp.projectplanning.domain.event;

import com.ccpp.shared.infrastructure.event.DomainEvent;
import com.ccpp.shared.domain.identities.ProjectId;

public record ProjectCreationCancelled(
        ProjectId projectId,
        String reason
) implements DomainEvent {
}
