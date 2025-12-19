package fr.joffreybonifay.ccpp.workspace.domain.event;

import fr.joffreybonifay.ccpp.shared.identities.WorkspaceId;
import fr.joffreybonifay.ccpp.shared.event.DomainEvent;

public record WorkspaceProjectLimitReached(
        WorkspaceId workspaceId
) implements DomainEvent {
}
