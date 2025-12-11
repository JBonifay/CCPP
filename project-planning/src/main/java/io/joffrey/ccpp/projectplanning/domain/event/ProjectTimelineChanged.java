package io.joffrey.ccpp.projectplanning.domain.event;

import com.ccpp.shared.domain.DomainEvent;
import com.ccpp.shared.identities.ProjectId;
import com.ccpp.shared.valueobjects.DateRange;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.time.Instant;

@Getter
@EqualsAndHashCode(callSuper = true)
public class ProjectTimelineChanged extends DomainEvent {

    private final ProjectId projectId;
    private final DateRange newTimeline;

    public ProjectTimelineChanged(ProjectId projectId, DateRange newTimeline) {
        super();
        this.projectId = projectId;
        this.newTimeline = newTimeline;
    }

}
