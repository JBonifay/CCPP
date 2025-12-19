package io.joffrey.ccpp.workspace.domain.event;

import com.ccpp.shared.identities.WorkspaceId;
import com.ccpp.shared.event.DomainEvent;

public record WorkspaceProjectCreationApproved(
        WorkspaceId workspaceId
) implements DomainEvent {
}
