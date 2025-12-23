package fr.joffreybonifay.ccpp.shared.domain.event;

import fr.joffreybonifay.ccpp.shared.domain.identities.ProjectId;
import fr.joffreybonifay.ccpp.shared.domain.identities.WorkspaceId;

public record ProjectCreationFailed(
    ProjectId projectId,
    WorkspaceId workspaceId,
    String reason
) implements DomainEvent {

}
