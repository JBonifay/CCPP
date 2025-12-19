package io.joffrey.ccpp.projectplanning.application.command.command;

import com.ccpp.shared.command.Command;
import com.ccpp.shared.identities.ProjectId;
import com.ccpp.shared.valueobjects.Money;
import io.joffrey.ccpp.projectplanning.domain.valueobject.BudgetItemId;

import java.util.UUID;

public record UpdateBudgetItemCommand(
        ProjectId projectId,
        BudgetItemId budgetItemId,
        String description,
        Money newAmount
) implements Command {
    @Override
    public UUID getCommandId() {
        return null;
    }

    @Override
    public UUID getAggregateId() {
        return null;
    }

    @Override
    public UUID getCorrelationId() {
        return null;
    }

    @Override
    public UUID getCausationId() {
        return null;
    }
}
