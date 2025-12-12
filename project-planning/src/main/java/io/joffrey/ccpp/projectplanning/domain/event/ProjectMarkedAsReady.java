package io.joffrey.ccpp.projectplanning.domain.event;

import com.ccpp.shared.identities.ProjectId;
import com.ccpp.shared.identities.UserId;
import com.ccpp.shared.identities.WorkspaceId;
import io.joffrey.ccpp.projectplanning.domain.ProjectDomainEvent;
import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent = true)
public final class ProjectMarkedAsReady extends ProjectDomainEvent {

    private final WorkspaceId workspaceId;
    private final UserId userId;

    public ProjectMarkedAsReady(ProjectId projectId, WorkspaceId workspaceId, UserId userId, Integer eventSequence) {
        super(projectId, eventSequence);
        this.workspaceId = workspaceId;
        this.userId = userId;
    }

}
