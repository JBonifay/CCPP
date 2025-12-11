package io.joffrey.ccpp.projectplanning.domain.event;

import com.ccpp.shared.domain.DomainEvent;
import com.ccpp.shared.identities.ProjectId;
import com.ccpp.shared.identities.UserId;
import com.ccpp.shared.identities.WorkspaceId;

public record ProjectMarkedAsReady(
    ProjectId projectId,
    WorkspaceId workspaceId,
    UserId userId
) implements DomainEvent {
}
