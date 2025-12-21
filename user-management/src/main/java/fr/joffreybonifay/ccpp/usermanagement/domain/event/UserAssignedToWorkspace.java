package fr.joffreybonifay.ccpp.usermanagement.domain.event;

import fr.joffreybonifay.ccpp.shared.event.DomainEvent;
import fr.joffreybonifay.ccpp.shared.identities.UserId;
import fr.joffreybonifay.ccpp.shared.identities.WorkspaceId;

public record UserAssignedToWorkspace(
        UserId userId,
        WorkspaceId workspaceId
) implements DomainEvent {
}
