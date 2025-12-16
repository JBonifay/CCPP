package io.joffrey.ccpp.projectplanning.domain.event;

import com.ccpp.shared.domain.identities.ProjectId;
import com.ccpp.shared.infrastructure.event.DomainEvent;

public record ProjectDetailsUpdated(
        ProjectId projectId,
        String title,
        String description
) implements DomainEvent {

}
