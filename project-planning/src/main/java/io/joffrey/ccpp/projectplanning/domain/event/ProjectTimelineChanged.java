package io.joffrey.ccpp.projectplanning.domain.event;

import com.ccpp.shared.identities.ProjectId;
import com.ccpp.shared.valueobjects.DateRange;
import com.ccpp.shared.event.DomainEvent;

public record ProjectTimelineChanged(
        ProjectId projectId,
        DateRange newTimeline
) implements DomainEvent {

}
