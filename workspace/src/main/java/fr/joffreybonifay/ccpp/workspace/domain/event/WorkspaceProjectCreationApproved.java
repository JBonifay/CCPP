package fr.joffreybonifay.ccpp.workspace.domain.event;

import fr.joffreybonifay.ccpp.shared.identities.WorkspaceId;
import fr.joffreybonifay.ccpp.shared.event.DomainEvent;

public record WorkspaceProjectCreationApproved(
        WorkspaceId workspaceId
) implements DomainEvent {
}
