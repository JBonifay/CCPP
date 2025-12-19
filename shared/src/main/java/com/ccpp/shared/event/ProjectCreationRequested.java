package com.ccpp.shared.event;

import com.ccpp.shared.identities.ProjectId;
import com.ccpp.shared.identities.UserId;
import com.ccpp.shared.identities.WorkspaceId;
import java.math.BigDecimal;

public record ProjectCreationRequested(
    ProjectId projectId,
    WorkspaceId workspaceId,
    UserId userId,
    String title,
    BigDecimal projectBudgetLimit
) implements DomainEvent {
}
