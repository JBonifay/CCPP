package com.ccpp.shared.event;

import com.ccpp.shared.identities.ProjectId;
import com.ccpp.shared.identities.WorkspaceId;

public record WorkspaceProjectCreationApproved(
    WorkspaceId workspaceId,
    ProjectId projectId
) implements DomainEvent {
}
