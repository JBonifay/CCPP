package fr.joffreybonifay.ccpp.projectplanning.domain.event;

import fr.joffreybonifay.ccpp.shared.identities.ProjectId;
import fr.joffreybonifay.ccpp.shared.event.DomainEvent;

public record ProjectDetailsUpdated(
        ProjectId projectId,
        String title,
        String description
) implements DomainEvent {

}
