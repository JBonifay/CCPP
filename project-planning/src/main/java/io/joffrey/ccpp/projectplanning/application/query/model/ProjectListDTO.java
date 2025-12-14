package io.joffrey.ccpp.projectplanning.application.query.model;

import com.ccpp.shared.identities.ProjectId;
import com.ccpp.shared.identities.WorkspaceId;

import java.math.BigDecimal;
import java.util.UUID;

public record ProjectListDTO(
        String projectId,
        String workspaceId,
        String title,
        String status,
        BigDecimal totalBudget,
        int participantCount
) {

    public ProjectListDTO(ProjectId projectId, WorkspaceId workspaceId, String title, String status, BigDecimal totalBudget, int participantCount) {
        this(projectId.value().toString(), workspaceId.value().toString(), title, status, totalBudget, participantCount);
    }

    public ProjectId getProjectId() {
        return new ProjectId(UUID.fromString(projectId));
    }

    public WorkspaceId getWorkspaceId() {
        return new WorkspaceId(UUID.fromString(workspaceId));
    }
}

