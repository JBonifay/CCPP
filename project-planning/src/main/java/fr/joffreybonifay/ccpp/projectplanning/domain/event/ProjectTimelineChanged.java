package fr.joffreybonifay.ccpp.projectplanning.domain.event;

import fr.joffreybonifay.ccpp.shared.identities.ProjectId;
import fr.joffreybonifay.ccpp.shared.valueobjects.DateRange;
import fr.joffreybonifay.ccpp.shared.event.DomainEvent;

public record ProjectTimelineChanged(
        ProjectId projectId,
        DateRange newTimeline
) implements DomainEvent {

}
