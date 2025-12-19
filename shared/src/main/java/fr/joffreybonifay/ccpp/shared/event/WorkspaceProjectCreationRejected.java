package fr.joffreybonifay.ccpp.shared.event;

import fr.joffreybonifay.ccpp.shared.identities.ProjectId;
import fr.joffreybonifay.ccpp.shared.identities.WorkspaceId;

public record WorkspaceProjectCreationRejected(
        WorkspaceId workspaceId,
        ProjectId projectId,
        String reason
) implements DomainEvent {
}
