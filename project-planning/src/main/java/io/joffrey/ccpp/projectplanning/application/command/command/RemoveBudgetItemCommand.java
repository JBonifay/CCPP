package io.joffrey.ccpp.projectplanning.application.command.command;

import com.ccpp.shared.command.Command;
import com.ccpp.shared.identities.ProjectId;
import io.joffrey.ccpp.projectplanning.domain.valueobject.BudgetItemId;

import java.util.UUID;

public record RemoveBudgetItemCommand(
        UUID commandId,
        ProjectId projectId,
        BudgetItemId budgetItemId,
        UUID correlationId,
        UUID causationId
)  implements Command {

    @Override
    public UUID aggregateId() {
        return null;
    }
}
