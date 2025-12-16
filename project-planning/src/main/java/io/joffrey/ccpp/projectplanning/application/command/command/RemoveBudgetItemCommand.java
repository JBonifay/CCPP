package io.joffrey.ccpp.projectplanning.application.command.command;

import com.ccpp.shared.infrastructure.command.Command;
import com.ccpp.shared.domain.identities.ProjectId;
import io.joffrey.ccpp.projectplanning.domain.valueobject.BudgetItemId;

public record RemoveBudgetItemCommand(
        ProjectId projectId,
        BudgetItemId budgetItemId
)  implements Command {
}
