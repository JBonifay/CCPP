package io.joffrey.ccpp.projectplanning.application.query;

import com.ccpp.shared.domain.identities.WorkspaceId;

public record GetProjectListQuery(
        WorkspaceId workspaceId
) {
}
