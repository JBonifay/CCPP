package fr.joffreybonifay.ccpp.projectplanning.application.query.model;

import fr.joffreybonifay.ccpp.projectplanning.domain.model.ProjectStatus;
import fr.joffreybonifay.ccpp.shared.domain.identities.ProjectId;
import fr.joffreybonifay.ccpp.shared.domain.identities.WorkspaceId;

import java.math.BigDecimal;

public record ProjectListDTO(
        ProjectId projectId,
        WorkspaceId workspaceId,
        String title,
        ProjectStatus status,
        BigDecimal totalBudget,
        BigDecimal actualBudget,
        int participantCount
) {

}

