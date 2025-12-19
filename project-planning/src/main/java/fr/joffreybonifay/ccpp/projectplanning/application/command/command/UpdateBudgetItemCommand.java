package fr.joffreybonifay.ccpp.projectplanning.application.command.command;

import fr.joffreybonifay.ccpp.shared.command.Command;
import fr.joffreybonifay.ccpp.shared.identities.ProjectId;
import fr.joffreybonifay.ccpp.shared.valueobjects.Money;
import fr.joffreybonifay.ccpp.projectplanning.domain.valueobject.BudgetItemId;

import java.util.UUID;

public record UpdateBudgetItemCommand(
        UUID commandId,
        ProjectId projectId,
        BudgetItemId budgetItemId,
        String description,
        Money newAmount,
        UUID correlationId
) implements Command {

    public UpdateBudgetItemCommand(ProjectId projectId, BudgetItemId budgetItemId, String description, Money newAmount, UUID correlationId, UUID causationId) {
        this(UUID.randomUUID(), projectId, budgetItemId, description, newAmount, correlationId);
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
