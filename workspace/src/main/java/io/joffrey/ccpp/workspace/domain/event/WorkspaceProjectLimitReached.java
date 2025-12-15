package io.joffrey.ccpp.workspace.domain.event;

import com.ccpp.shared.identities.WorkspaceId;

public record WorkspaceProjectLimitReached(
        WorkspaceId workspaceId
) implements WorkspaceDomainEvent {
}
