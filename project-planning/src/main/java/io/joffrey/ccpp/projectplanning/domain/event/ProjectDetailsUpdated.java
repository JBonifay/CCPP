package io.joffrey.ccpp.projectplanning.domain.event;

import com.ccpp.shared.domain.DomainEvent;
import com.ccpp.shared.identities.ProjectId;

public record ProjectDetailsUpdated(
        ProjectId projectId,
        String title,
        String description
) implements DomainEvent {
}
