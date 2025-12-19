package io.joffrey.ccpp.projectplanning.domain.event;

import com.ccpp.shared.identities.ProjectId;
import com.ccpp.shared.event.DomainEvent;

public record ProjectDetailsUpdated(
        ProjectId projectId,
        String title,
        String description
) implements DomainEvent {

}
