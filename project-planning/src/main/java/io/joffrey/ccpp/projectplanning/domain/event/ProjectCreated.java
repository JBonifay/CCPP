package io.joffrey.ccpp.projectplanning.domain.event;

import com.ccpp.shared.identities.ProjectId;
import com.ccpp.shared.identities.UserId;
import com.ccpp.shared.identities.WorkspaceId;
import com.ccpp.shared.valueobjects.DateRange;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

public record ProjectCreated(
        ProjectId projectId,
        WorkspaceId workspaceId,
        UserId userId,
        String title,
        String description,
        DateRange timeline,
        BigDecimal projectBudgetLimit
) implements ProjectDomainEvent {

}
