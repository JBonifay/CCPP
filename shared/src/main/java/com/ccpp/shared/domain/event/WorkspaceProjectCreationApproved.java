package com.ccpp.shared.domain.event;

import com.ccpp.shared.infrastructure.event.DomainEvent;
import com.ccpp.shared.domain.identities.ProjectId;
import com.ccpp.shared.domain.identities.WorkspaceId;

/**
 * Published by workspace when project creation quota is approved.
 * Consumed by project-planning to finalize project creation.
 * Part of choreographed project creation saga.
 */
public record WorkspaceProjectCreationApproved(
    WorkspaceId workspaceId,
    ProjectId projectId
) implements DomainEvent {
}
