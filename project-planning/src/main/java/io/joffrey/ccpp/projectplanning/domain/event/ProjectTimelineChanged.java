package io.joffrey.ccpp.projectplanning.domain.event;

import com.ccpp.shared.domain.identities.ProjectId;
import com.ccpp.shared.domain.valueobjects.DateRange;
import com.ccpp.shared.infrastructure.event.DomainEvent;

public record ProjectTimelineChanged(
        ProjectId projectId,
        DateRange newTimeline
) implements DomainEvent {

}
