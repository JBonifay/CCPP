package fr.joffreybonifay.ccpp.shared.event;

import fr.joffreybonifay.ccpp.shared.identities.ProjectId;
import fr.joffreybonifay.ccpp.shared.identities.WorkspaceId;

public record ProjectCreationFailed(
    ProjectId projectId,
    WorkspaceId workspaceId,
    String reason
) implements DomainEvent {

}
