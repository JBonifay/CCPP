package io.joffrey.ccpp.projectplanning.domain.event;

import com.ccpp.shared.identities.ProjectId;
import com.ccpp.shared.identities.UserId;
import com.ccpp.shared.identities.WorkspaceId;
import com.ccpp.shared.valueobjects.DateRange;
import io.joffrey.ccpp.projectplanning.domain.ProjectDomainEvent;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

@Getter
@Accessors(fluent = true)
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public final class ProjectCreated extends ProjectDomainEvent {

    private final WorkspaceId workspaceId;
    private final UserId userId;
    private final String title;
    private final String description;
    private final DateRange timeline;
    private final BigDecimal projectBudgetLimit;

    public ProjectCreated(
            ProjectId projectId,
            WorkspaceId workspaceId,
            UserId userId,
            String title,
            String description,
            DateRange timeline,
            BigDecimal projectBudgetLimit,
            Integer eventSequence
    ) {
        super(projectId, eventSequence);
        this.workspaceId = workspaceId;
        this.userId = userId;
        this.title = title;
        this.description = description;
        this.timeline = timeline;
        this.projectBudgetLimit = projectBudgetLimit;
    }

}
