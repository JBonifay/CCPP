package fr.joffreybonifay.ccpp.projectplanning.domain.event;

import fr.joffreybonifay.ccpp.shared.identities.ProjectId;
import fr.joffreybonifay.ccpp.shared.identities.UserId;
import fr.joffreybonifay.ccpp.shared.identities.WorkspaceId;
import fr.joffreybonifay.ccpp.shared.event.DomainEvent;

public record ProjectMarkedAsReady(
        ProjectId projectId,
        WorkspaceId workspaceId,
        UserId userId
) implements DomainEvent {
}
