package fr.joffreybonifay.ccpp.projectplanning.application.command.command;

import fr.joffreybonifay.ccpp.shared.command.Command;
import fr.joffreybonifay.ccpp.shared.identities.ProjectId;
import fr.joffreybonifay.ccpp.shared.identities.UserId;
import fr.joffreybonifay.ccpp.shared.identities.WorkspaceId;
import fr.joffreybonifay.ccpp.shared.valueobjects.DateRange;

import java.math.BigDecimal;
import java.util.UUID;

public record RequestProjectCreationCommand(
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

    public RequestProjectCreationCommand(
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
    public UUID correlationId() {
        return correlationId != null ? correlationId : commandId;
    }

    @Override
    public UUID causationId() {
        return commandId;
    }
}
