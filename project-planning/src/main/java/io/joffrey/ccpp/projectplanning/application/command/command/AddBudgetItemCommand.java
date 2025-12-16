package io.joffrey.ccpp.projectplanning.application.command.command;

import com.ccpp.shared.infrastructure.command.Command;
import com.ccpp.shared.domain.identities.ProjectId;
import com.ccpp.shared.domain.valueobjects.Money;
import io.joffrey.ccpp.projectplanning.domain.valueobject.BudgetItemId;

public record AddBudgetItemCommand(
        ProjectId projectId,
        BudgetItemId budgetItemId,
        String description,
        Money amount
) implements Command {
}
