package com.ccpp.shared.domain.event;

import com.ccpp.shared.domain.DomainEvent;
import com.ccpp.shared.identities.ProjectId;
import com.ccpp.shared.identities.WorkspaceId;

public record WorkspaceProjectCreationRejected(
        WorkspaceId workspaceId,
        ProjectId projectId,
        String reason
) implements DomainEvent {
}
