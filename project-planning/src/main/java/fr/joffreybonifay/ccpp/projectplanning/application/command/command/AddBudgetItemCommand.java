package fr.joffreybonifay.ccpp.projectplanning.application.command.command;

import fr.joffreybonifay.ccpp.shared.command.Command;
import fr.joffreybonifay.ccpp.shared.domain.identities.ProjectId;
import fr.joffreybonifay.ccpp.shared.domain.valueobjects.Money;
import fr.joffreybonifay.ccpp.projectplanning.domain.valueobject.BudgetItemId;

import java.util.UUID;

public record AddBudgetItemCommand(
        UUID commandId,
        ProjectId projectId,
        BudgetItemId budgetItemId,
        String description,
        Money amount,
        UUID correlationId,
        UUID causationId
) implements Command {


    public AddBudgetItemCommand(ProjectId projectId, BudgetItemId budgetItemId, String description, Money amount, UUID correlationId, UUID causationId) {
        this(UUID.randomUUID(), projectId, budgetItemId, description, amount, correlationId, causationId);
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
