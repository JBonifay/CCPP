package io.joffrey.ccpp.workspace.domain.event;

import com.ccpp.shared.domain.DomainEvent;
import com.ccpp.shared.identities.WorkspaceId;

public sealed interface WorkspaceDomainEvent extends DomainEvent permits WorkspaceCreated,
                                                                         WorkspaceProjectCreationApproved,
                                                                         WorkspaceProjectLimitReached,
                                                                         WorkspaceSubscriptionUpgraded {
    WorkspaceId workspaceId();
}
