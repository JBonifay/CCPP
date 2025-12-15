package io.joffrey.ccpp.workspace.domain.event;

import com.ccpp.shared.domain.DomainEvent;

public sealed interface WorkspaceDomainEvent extends DomainEvent permits WorkspaceCreated,
        WorkspaceProjectCreationApproved, WorkspaceProjectLimitReached, WorkspaceSubscriptionUpgraded {
}
