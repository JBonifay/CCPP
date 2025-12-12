package io.joffrey.ccpp.projectplanning.domain.event;

import com.ccpp.shared.identities.ProjectId;
import com.ccpp.shared.valueobjects.DateRange;
import io.joffrey.ccpp.projectplanning.domain.ProjectDomainEvent;
import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent = true)
public final class ProjectTimelineChanged extends ProjectDomainEvent {

    private final DateRange newTimeline;

    public ProjectTimelineChanged(ProjectId projectId, DateRange newTimeline, Integer eventSequence) {
        super(projectId, eventSequence);
        this.newTimeline = newTimeline;
    }
}
