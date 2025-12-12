package io.joffrey.ccpp.projectplanning.domain.event;

import com.ccpp.shared.identities.ProjectId;
import com.ccpp.shared.identities.UserId;
import com.ccpp.shared.identities.WorkspaceId;
import lombok.Getter;
import lombok.experimental.Accessors;

public record ProjectMarkedAsReady(
        ProjectId projectId,
        WorkspaceId workspaceId,
        UserId userId
) implements ProjectDomainEvent {
}
