package fr.joffreybonifay.ccpp.shared.domain.event;

import fr.joffreybonifay.ccpp.shared.domain.identities.ProjectId;
import fr.joffreybonifay.ccpp.shared.domain.identities.WorkspaceId;

public record WorkspaceProjectCreationRejected(
        WorkspaceId workspaceId,
        ProjectId projectId,
        String reason
) implements DomainEvent {
}
