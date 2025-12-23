package fr.joffreybonifay.ccpp.usermanagement.domain.event;

import fr.joffreybonifay.ccpp.shared.domain.event.DomainEvent;
import fr.joffreybonifay.ccpp.shared.domain.identities.UserId;
import fr.joffreybonifay.ccpp.shared.domain.identities.WorkspaceId;

public record UserAssignedToWorkspace(
        UserId userId,
        WorkspaceId workspaceId
) implements DomainEvent {
}
