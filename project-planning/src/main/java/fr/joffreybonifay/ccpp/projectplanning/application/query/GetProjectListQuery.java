package fr.joffreybonifay.ccpp.projectplanning.application.query;

import fr.joffreybonifay.ccpp.shared.domain.identities.WorkspaceId;

public record GetProjectListQuery(
        WorkspaceId workspaceId
) {
}
