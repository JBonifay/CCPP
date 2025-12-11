package io.joffrey.ccpp.projectplanning.domain.event;

import com.ccpp.shared.domain.DomainEvent;
import com.ccpp.shared.identities.ProjectId;
import com.ccpp.shared.identities.UserId;
import com.ccpp.shared.identities.WorkspaceId;
import com.ccpp.shared.valueobjects.DateRange;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ProjectCreated extends DomainEvent {

    private final ProjectId projectId;
    private final WorkspaceId workspaceId;
    private final UserId userId;
    private final String title;
    private final String description;
    private final DateRange timeline;
    private final BigDecimal projectBudgetLimit;

    public ProjectCreated(ProjectId projectId, WorkspaceId workspaceId, UserId userId, String title, String description, DateRange timeline, BigDecimal projectBudgetLimit) {
        this.workspaceId = workspaceId;
        this.userId = userId;
        this.projectId = projectId;
        this.title = title;
        this.description = description;
        this.timeline = timeline;
        this.projectBudgetLimit = projectBudgetLimit;
    }

    public ProjectCreated(ProjectId projectId, WorkspaceId workspaceId, UserId userId, String title, String description, DateRange timeline, BigDecimal projectBudgetLimit, Instant occurredOn, int version) {
        super(projectId.value(), occurredOn, version);
        this.workspaceId = workspaceId;
        this.userId = userId;
        this.projectId = projectId;
        this.title = title;
        this.description = description;
        this.timeline = timeline;
        this.projectBudgetLimit = projectBudgetLimit;
    }
}
