package fr.joffreybonifay.ccpp.projectplanning.domain.event;

import fr.joffreybonifay.ccpp.shared.event.DomainEvent;
import fr.joffreybonifay.ccpp.shared.identities.ProjectId;

public record ProjectCreationCancelled(
        ProjectId projectId,
        String reason
) implements DomainEvent {
}
