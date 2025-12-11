package io.joffrey.ccpp.projectplanning.application.command;

import com.ccpp.shared.identities.ProjectId;
import com.ccpp.shared.identities.UserId;
import com.ccpp.shared.identities.WorkspaceId;
import com.ccpp.shared.valueobjects.DateRange;

import java.math.BigDecimal;

public record CreateProjectCommand(
        WorkspaceId workspaceId,
        UserId userId,
        ProjectId projectId,
        String title,
        String description,
        DateRange timeline,
        BigDecimal budgetLimit
) {
}
