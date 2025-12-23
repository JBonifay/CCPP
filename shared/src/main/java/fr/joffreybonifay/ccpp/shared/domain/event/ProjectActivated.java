package fr.joffreybonifay.ccpp.shared.domain.event;

import fr.joffreybonifay.ccpp.shared.domain.identities.ProjectId;
import fr.joffreybonifay.ccpp.shared.domain.identities.WorkspaceId;

public record ProjectActivated(
    ProjectId projectId,
    WorkspaceId workspaceId
) implements DomainEvent {

}
