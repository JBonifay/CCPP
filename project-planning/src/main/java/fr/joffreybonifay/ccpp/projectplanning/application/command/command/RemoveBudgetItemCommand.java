package fr.joffreybonifay.ccpp.projectplanning.application.command.command;

import fr.joffreybonifay.ccpp.shared.command.Command;
import fr.joffreybonifay.ccpp.shared.identities.ProjectId;
import fr.joffreybonifay.ccpp.projectplanning.domain.valueobject.BudgetItemId;

import java.util.UUID;

public record RemoveBudgetItemCommand(
        UUID commandId,
        ProjectId projectId,
        BudgetItemId budgetItemId,
        UUID correlationId
)  implements Command {

    public RemoveBudgetItemCommand( ProjectId projectId, BudgetItemId budgetItemId, UUID correlationId) {
        this(UUID.randomUUID(), projectId, budgetItemId, correlationId);
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
