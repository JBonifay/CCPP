package io.joffrey.ccpp.projectplanning.application.command.command;

import com.ccpp.shared.command.Command;
import com.ccpp.shared.identities.ProjectId;
import com.ccpp.shared.valueobjects.Money;
import io.joffrey.ccpp.projectplanning.domain.valueobject.BudgetItemId;

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
