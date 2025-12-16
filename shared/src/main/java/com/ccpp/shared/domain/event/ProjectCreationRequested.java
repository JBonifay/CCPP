package com.ccpp.shared.domain.event;

import com.ccpp.shared.infrastructure.event.DomainEvent;
import com.ccpp.shared.domain.identities.ProjectId;
import com.ccpp.shared.domain.identities.UserId;
import com.ccpp.shared.domain.identities.WorkspaceId;
import java.math.BigDecimal;

public record ProjectCreationRequested(
    ProjectId projectId,
    WorkspaceId workspaceId,
    UserId userId,
    String title,
    BigDecimal projectBudgetLimit
) implements DomainEvent {
}
