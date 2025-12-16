package io.joffrey.ccpp.projectplanning.application.query.model;

import com.ccpp.shared.identities.ProjectId;
import com.ccpp.shared.identities.WorkspaceId;

import io.joffrey.ccpp.projectplanning.domain.model.ProjectStatus;
import java.math.BigDecimal;

public record ProjectListDTO(
        ProjectId projectId,
        WorkspaceId workspaceId,
        String title,
        ProjectStatus status,
        BigDecimal totalBudget,
        int participantCount
) {
}

