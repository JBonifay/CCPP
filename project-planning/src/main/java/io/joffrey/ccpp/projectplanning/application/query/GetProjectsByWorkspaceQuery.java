package io.joffrey.ccpp.projectplanning.application.query;

import com.ccpp.shared.identities.WorkspaceId;

public record GetProjectsByWorkspaceQuery(
        WorkspaceId workspaceId
) {
}
