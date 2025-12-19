package io.joffrey.ccpp.projectplanning.application.command.command;

import com.ccpp.shared.command.Command;
import com.ccpp.shared.identities.ProjectId;
import com.ccpp.shared.identities.UserId;
import com.ccpp.shared.identities.WorkspaceId;
import com.ccpp.shared.valueobjects.DateRange;

import java.math.BigDecimal;
import java.util.UUID;

public record CreateProjectCommand(
        UUID commandId,
        WorkspaceId workspaceId,
        UserId userId,
        ProjectId projectId,
        String title,
        String description,
        DateRange timeline,
        BigDecimal budgetLimit,
        UUID correlationId
) implements Command {

    public CreateProjectCommand(
            WorkspaceId workspaceId,
            UserId userId,
            ProjectId projectId,
            String title,
            String description,
            DateRange timeline,
            BigDecimal budgetLimit,
            UUID correlationId
    ) {
        this(UUID.randomUUID(), workspaceId, userId, projectId, title, description, timeline, budgetLimit, correlationId);
    }

    @Override
    public UUID aggregateId() {
        return projectId.value();
    }

    @Override
    public UUID correlationId() {
        return correlationId != null ? correlationId : commandId;
    }

    @Override
    public UUID causationId() {
        return commandId;
    }
}
