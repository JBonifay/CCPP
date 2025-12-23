package fr.joffreybonifay.ccpp.projectplanning.application.query;

import fr.joffreybonifay.ccpp.shared.domain.identities.ProjectId;
import fr.joffreybonifay.ccpp.shared.domain.identities.WorkspaceId;

public record GetProjectDetailQuery(
        ProjectId projectId,
        WorkspaceId workspaceId
) {
}
