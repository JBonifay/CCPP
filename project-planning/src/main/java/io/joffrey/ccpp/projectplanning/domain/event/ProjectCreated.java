package io.joffrey.ccpp.projectplanning.domain.event;

import com.ccpp.shared.domain.DomainEvent;
import com.ccpp.shared.identities.ProjectId;
import com.ccpp.shared.identities.UserId;
import com.ccpp.shared.identities.WorkspaceId;
import com.ccpp.shared.valueobjects.DateRange;

import java.math.BigDecimal;

public record ProjectCreated(
        WorkspaceId workspaceId,
        UserId userId,
        ProjectId projectId,
        String title,
        String description,
        DateRange timeline,
        BigDecimal projectBudgetLimit
) implements DomainEvent {
}
