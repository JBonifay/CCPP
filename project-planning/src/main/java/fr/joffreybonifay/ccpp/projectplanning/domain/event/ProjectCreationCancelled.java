package fr.joffreybonifay.ccpp.projectplanning.domain.event;

import fr.joffreybonifay.ccpp.shared.domain.event.DomainEvent;
import fr.joffreybonifay.ccpp.shared.domain.identities.ProjectId;

public record ProjectCreationCancelled(
        ProjectId projectId,
        String reason
) implements DomainEvent {
}
