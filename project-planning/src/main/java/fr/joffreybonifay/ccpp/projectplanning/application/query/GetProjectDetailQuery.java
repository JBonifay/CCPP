package fr.joffreybonifay.ccpp.projectplanning.application.query;

import fr.joffreybonifay.ccpp.shared.identities.ProjectId;
import fr.joffreybonifay.ccpp.shared.identities.WorkspaceId;

public record GetProjectDetailQuery(
        ProjectId projectId,
        WorkspaceId workspaceId
) {
}
