package io.joffrey.ccpp.projectplanning.application.query;

import com.ccpp.shared.identities.ProjectId;
import com.ccpp.shared.identities.WorkspaceId;

public record GetProjectDetailQuery(
        ProjectId projectId,
        WorkspaceId workspaceId
) {
}
