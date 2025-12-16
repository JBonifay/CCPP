package io.joffrey.ccpp.projectplanning.domain.event;

import com.ccpp.shared.domain.identities.ProjectId;
import com.ccpp.shared.domain.identities.UserId;
import com.ccpp.shared.domain.identities.WorkspaceId;
import com.ccpp.shared.domain.valueobjects.DateRange;
import com.ccpp.shared.infrastructure.event.DomainEvent;

import java.math.BigDecimal;

public record ProjectCreated(
    ProjectId projectId,
    WorkspaceId workspaceId,
    UserId userId,
    String title,
    String description,
    DateRange timeline,
    BigDecimal projectBudgetLimit
) implements DomainEvent {

}
