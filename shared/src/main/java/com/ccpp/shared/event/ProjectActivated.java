package com.ccpp.shared.event;

import com.ccpp.shared.identities.ProjectId;
import com.ccpp.shared.identities.WorkspaceId;

public record ProjectActivated(
    ProjectId projectId,
    WorkspaceId workspaceId
) implements DomainEvent {

}
