package fr.joffreybonifay.ccpp.workspace.domain.event;

import fr.joffreybonifay.ccpp.shared.domain.identities.WorkspaceId;
import fr.joffreybonifay.ccpp.shared.domain.event.DomainEvent;

public record WorkspaceProjectLimitReached(
        WorkspaceId workspaceId
) implements DomainEvent {
}
