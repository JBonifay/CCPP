package com.ccpp.shared.domain.event;

import com.ccpp.shared.infrastructure.event.DomainEvent;
import com.ccpp.shared.domain.identities.ProjectId;
import com.ccpp.shared.domain.identities.WorkspaceId;

public record ProjectActivated(
    ProjectId projectId,
    WorkspaceId workspaceId
) implements DomainEvent {

}
