package com.ccpp.shared.event;

import com.ccpp.shared.identities.ProjectId;
import com.ccpp.shared.identities.WorkspaceId;

public record ProjectCreationFailed(
    ProjectId projectId,
    WorkspaceId workspaceId,
    String reason
) implements DomainEvent {

}
