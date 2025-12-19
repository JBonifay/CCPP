package fr.joffreybonifay.ccpp.shared.event;

import fr.joffreybonifay.ccpp.shared.identities.ProjectId;
import fr.joffreybonifay.ccpp.shared.identities.WorkspaceId;

public record WorkspaceProjectCreationApproved(
    WorkspaceId workspaceId,
    ProjectId projectId
) implements DomainEvent {
}
