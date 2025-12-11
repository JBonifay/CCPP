package io.joffrey.ccpp.projectplanning.domain.event;

import com.ccpp.shared.domain.DomainEvent;
import com.ccpp.shared.identities.ProjectId;
import com.ccpp.shared.valueobjects.DateRange;

public record ProjectTimelineChanged(
        ProjectId projectId, DateRange newTimeline
) implements DomainEvent {

}
