package fr.joffreybonifay.ccpp.usermanagement.domain.event;

import fr.joffreybonifay.ccpp.shared.domain.event.DomainEvent;
import fr.joffreybonifay.ccpp.shared.domain.identities.UserId;
import fr.joffreybonifay.ccpp.shared.domain.identities.WorkspaceId;
import fr.joffreybonifay.ccpp.usermanagement.domain.model.UserRole;

public record UserAssignedToWorkspace(
        UserId userId,
        UserRole userRole,
        WorkspaceId workspaceId,
        String workspaceName,
        String workspaceLogoUrl
) implements DomainEvent {
}
