package io.joffrey.ccpp.projectplanning.application.query;

import com.ccpp.shared.domain.identities.ProjectId;
import com.ccpp.shared.domain.identities.WorkspaceId;

public record GetProjectDetailQuery(
        ProjectId projectId,
        WorkspaceId workspaceId
) {
}
