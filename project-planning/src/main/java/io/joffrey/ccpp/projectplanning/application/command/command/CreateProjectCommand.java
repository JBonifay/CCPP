package io.joffrey.ccpp.projectplanning.application.command.command;

import com.ccpp.shared.infrastructure.command.Command;
import com.ccpp.shared.domain.identities.ProjectId;
import com.ccpp.shared.domain.identities.UserId;
import com.ccpp.shared.domain.identities.WorkspaceId;
import com.ccpp.shared.domain.valueobjects.DateRange;

import java.math.BigDecimal;

public record CreateProjectCommand(
        WorkspaceId workspaceId,
        UserId userId,
        ProjectId projectId,
        String title,
        String description,
        DateRange timeline,
        BigDecimal budgetLimit
) implements Command {
}
