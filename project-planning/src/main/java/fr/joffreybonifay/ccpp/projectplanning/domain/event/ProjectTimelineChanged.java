package fr.joffreybonifay.ccpp.projectplanning.domain.event;

import fr.joffreybonifay.ccpp.shared.domain.identities.ProjectId;
import fr.joffreybonifay.ccpp.shared.domain.valueobjects.DateRange;
import fr.joffreybonifay.ccpp.shared.domain.event.DomainEvent;

public record ProjectTimelineChanged(
        ProjectId projectId,
        DateRange newTimeline
) implements DomainEvent {

}
