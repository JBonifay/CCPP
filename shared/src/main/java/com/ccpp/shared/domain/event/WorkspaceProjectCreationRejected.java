package com.ccpp.shared.domain.event;

import com.ccpp.shared.infrastructure.event.DomainEvent;
import com.ccpp.shared.domain.identities.ProjectId;
import com.ccpp.shared.domain.identities.WorkspaceId;

public record WorkspaceProjectCreationRejected(
        WorkspaceId workspaceId,
        ProjectId projectId,
        String reason
) implements DomainEvent {
}
