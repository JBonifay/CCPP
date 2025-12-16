package io.joffrey.ccpp.workspace.domain.event;

import com.ccpp.shared.domain.identities.WorkspaceId;
import com.ccpp.shared.infrastructure.event.DomainEvent;

public record WorkspaceProjectLimitReached(
        WorkspaceId workspaceId
) implements DomainEvent {
}
