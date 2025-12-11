package io.joffrey.ccpp.projectplanning.domain.event;

import com.ccpp.shared.domain.DomainEvent;
import com.ccpp.shared.identities.ProjectId;
import com.ccpp.shared.identities.UserId;
import com.ccpp.shared.identities.WorkspaceId;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.time.Instant;

@Getter
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ProjectMarkedAsReady extends DomainEvent {

    private final ProjectId projectId;
    private final WorkspaceId workspaceId;
    private final UserId userId;

    public ProjectMarkedAsReady(ProjectId projectId, WorkspaceId workspaceId, UserId userId) {
        super();
        this.projectId = projectId;
        this.workspaceId = workspaceId;
        this.userId = userId;
    }
}
